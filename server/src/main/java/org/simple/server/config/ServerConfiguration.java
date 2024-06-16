package org.simple.server.config;

import org.simple.server.Constants;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.StringDecoder;

@Configuration
public class ServerConfiguration {
    @Bean
    RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return strategies -> strategies
                .metadataExtractorRegistry(registry -> {
                    registry.metadataToExtract(Constants.CUSTOM_HEADER_MIMETYPE, String.class, Constants.CUSTOM_HEADER);
                })
                .decoders(decoders -> decoders.add(StringDecoder.allMimeTypes()));
    }
}