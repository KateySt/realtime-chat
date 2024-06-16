package org.simple.server.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.simple.server.ConditionFlag;
import org.simple.server.Constants;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@AllArgsConstructor
@Log4j2
@Controller
public class ServerController {
    @MessageMapping("responder-request-response.{id}")
    public Mono<String> handleRequestResponse(@DestinationVariable Integer id,
                                              @Header(Constants.CUSTOM_HEADER) String customHeader,
                                              @Headers Map<String, Object> metadata,
                                              @Payload String payload) {
        log.info("Request / Response");
        log.info("Custom header value is " + customHeader);
        return Mono.just("Hello " + payload + " with id of " + id)
                .doOnNext(log::info);
    }

    @MessageMapping("responder-channel-stream")
    public Flux<String> handleStream(@Payload Flux<String> payloads) {
        log.info("Channel Stream");
        return payloads.map(request -> request.toUpperCase(Locale.ROOT))
                .doOnNext(log::info);
    }

    @MessageMapping("responder-fire-forget")
    public void handleFireAndForget(String payload) {
        log.info("Fire and Forget");
        log.info(payload);
    }

    @MessageMapping("responder-channel-bidirectional")
    public Flux<String> handleBidirectional(RSocketRequester client, @Payload String request) {
        log.info("Bi-directional " + request);
        Flux<ConditionFlag> healthFlux = client.route("health")
                .data(Mono.just("STARTED?"))
                .retrieveFlux(ConditionFlag.class)
                .filter(chs -> chs.getState().equalsIgnoreCase(ConditionFlag.STOPPED))
                .doOnNext(chs -> log.info(chs.toString()));

        Flux<String> replyPayload = Flux.fromStream(Stream.generate(() -> ("Hello " + request + " @ " + Instant.now())))
                .delayElements(Duration.ofSeconds(1));
        return replyPayload.takeUntilOther(healthFlux)
                .doOnNext(log::info);
    }

    @MessageMapping("error")
    public Mono<String> error() {
        return Mono.error(new RuntimeException("Something bad happened"));
    }

    @MessageExceptionHandler(RuntimeException.class)
    public Mono<RuntimeException> exceptionHandler(RuntimeException runtimeException) {
        log.error(runtimeException.getMessage());
        return Mono.error(runtimeException);
    }
}
