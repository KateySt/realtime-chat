package org.simple.client.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;

@Configuration
public class ClientConfiguration {

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder, RSocketMessageHandler handler) {
        return builder
                .rsocketStrategies(b -> b.encoder(new SimpleAuthenticationEncoder()))
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 8181);
    }
}
