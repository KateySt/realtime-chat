package org.simple.client.controller;

import io.rsocket.metadata.WellKnownMimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.simple.client.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.IntStream;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ClientController {
    private final RSocketRequester rSocketRequester;

    @GetMapping("/request-response")
    public Mono<String> sendRequestResponse() {
        log.info("Sending request / response");
        return rSocketRequester.route("responder-request-response.{id}", 123)
                .metadata("some-custom-header-value", MimeType.valueOf("messaging/custom-header"))
                .data("data to send")
                .retrieveMono(String.class);
    }

    @GetMapping("/auth")
    public Mono<String> sendSimpleAuthentication() {
        return rSocketRequester.route("auth")
                .metadata(new UsernamePasswordMetadata("user2", "password2"),
                        MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .data(Mono.empty())
                .retrieveMono(String.class);
    }

    @GetMapping(value = "/channel-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendStream() {
        log.info("Sending channel stream ...");
        Flux<String> data = Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(i -> UUID.randomUUID().toString());
        return rSocketRequester.route("responder-channel-stream")
                .data(data)
                .retrieveFlux(String.class)
                .doOnNext(log::info);
    }

    @GetMapping("/channel-bidirectional")
    public void sendBidirectional() {
        /* NOTE: multiple clients can be configured with below
        int max = Math.max(5, (int) (Math.random() * 10));
         */
        int max = 1;
        log.info("Launching " + max + " clients");
        Flux.fromStream(IntStream.range(0, max).boxed())
                .delayElements(Duration.ofSeconds(1))
                .map(id -> new ClientService(rSocketRequester, id.toString()))
                .flatMap(ClientService::request)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(log::info);
    }

    @GetMapping("/fire-forget")
    public void sendFireAndForget() {
        log.info("Sending fire-and-forget");
        rSocketRequester.route("responder-fire-forget")
                .data(UUID.randomUUID().toString())
                .send()
                .subscribe();
    }


    @GetMapping("/error")
    public Mono<String> causeError() {
        return rSocketRequester.route("error")
                .data(UUID.randomUUID().toString())
                .retrieveMono(String.class)
                .doOnError(log::error)
                .onErrorReturn("error returned from service");
    }
}
