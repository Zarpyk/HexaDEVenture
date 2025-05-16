package com.hexadeventure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game-service")
@Getter
@Setter
public class GameServiceConfig {
    private String host;
    private int port;
}
