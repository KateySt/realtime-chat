package org.simple.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

@Log4j2
@RequiredArgsConstructor
public class ClientService {
    private final RSocketRequester rSocketRequester;
    private final String id;

    public Flux<String> request() {
        return rSocketRequester.route("responder-channel-bidirectional")
                .data("Client #" + id)
                .retrieveFlux(String.class);
    }
}
