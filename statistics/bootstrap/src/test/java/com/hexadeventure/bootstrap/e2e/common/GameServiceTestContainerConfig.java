package com.hexadeventure.bootstrap.e2e.common;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Profile("test")
@Testcontainers
public class GameServiceTestContainerConfig {
    
    private static final ComposeContainer container = new ComposeContainer(new File(
            "src/test/resources/docker-compose-test.yaml"))
            .withLocalCompose(true)
            .withExposedService("main", 8080)
            .waitingFor("main",
                        Wait.forHttp("/actuator/health")
                            .forPort(8080)
                            .forStatusCode(HttpStatus.OK.value())
                            .withStartupTimeout(Duration.ofSeconds(30)));
    
    static {
        container.start();
    }
    
    public static ComposeContainer getInstance() {
        return container;
    }
}
