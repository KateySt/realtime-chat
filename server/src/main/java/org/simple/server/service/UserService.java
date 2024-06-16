package org.simple.server.service;

import org.simple.server.entity.UserDetailsImpl;
import org.simple.server.entity.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    public List<UserDetailsImpl> users = List.of(
            new UserDetailsImpl("jay", "{noop}pw", Role.USER),
            new UserDetailsImpl("user2", "{noop}password2", Role.USER),
            new UserDetailsImpl("user3", "{noop}password3", Role.USER)
    );

    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        return Mono.justOrEmpty(users.stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username + " not found")))
                .map(user -> UserDetailsImpl.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build());
    }
}
