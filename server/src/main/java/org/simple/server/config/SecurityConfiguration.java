package org.simple.server.config;

import org.simple.server.entity.UserDetailsImpl;
import org.simple.server.entity.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {
    public List<UserDetailsImpl> users = List.of(
            new UserDetailsImpl("jay", "{noop}pw", Role.USER),
            new UserDetailsImpl("user2", "{noop}password2", Role.USER),
            new UserDetailsImpl("user3", "{noop}password3", Role.USER)
    );

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> {
            UserDetailsImpl user = users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

            return Mono.just(new UserDetailsImpl(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole()));
        };
    }
/*
    @Bean
    public KeyPair keyPair() throws NoSuchAlgorithmException {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(KeyPair keyPair) {
        var jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate()).build();
        var jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        return security
                .authorizePayload(authorize ->
                        authorize.route("auth").authenticated()
                                .anyRequest().permitAll()
                                .anyExchange().permitAll()
                )
                .simpleAuthentication(Customizer.withDefaults())
                .build();
    }

    @Bean
    RSocketMessageHandler rSocketMessageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer()
                .addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }
}
