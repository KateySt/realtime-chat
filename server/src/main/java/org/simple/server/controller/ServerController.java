package org.simple.server.controller;

import lombok.extern.log4j.Log4j2;
import org.simple.server.Constants;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;

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
