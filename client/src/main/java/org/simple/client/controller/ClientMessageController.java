package org.simple.client.controller;

import lombok.extern.log4j.Log4j2;
import org.simple.client.ConditionFlag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

@Log4j2
@Controller
public class ClientMessageController {

    @MessageMapping("health")
    public Flux<ConditionFlag> health(@Payload String payload) {
        log.info(payload);
        var start = new Date().getTime();
        var delay = Duration.ofSeconds(3).toMillis();
        return Flux.fromStream(Stream.generate(() -> {
                    var now = new Date().getTime();
                    var stop = (start + delay) < now;
                    return new ConditionFlag(stop ? ConditionFlag.STOPPED : ConditionFlag.STARTED);
                }))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(chs -> log.info("Sending status " + chs.getState()));
    }

}
