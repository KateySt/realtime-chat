package org.simple.server;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
