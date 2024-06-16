package org.simple.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Log4j2
@Controller
@RequiredArgsConstructor
class RestrictedController {

    @ConnectMapping("connect")
    public void connect(@AuthenticationPrincipal Mono<UserDetails> user) {
        user.map(UserDetails::getUsername)
                .subscribe(name -> log.info("Connection established for " + name));
    }

    @MessageMapping("auth")
    public Mono<String> authenticate(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user.map(u -> "Hi there " + u.getUsername());
    }
}