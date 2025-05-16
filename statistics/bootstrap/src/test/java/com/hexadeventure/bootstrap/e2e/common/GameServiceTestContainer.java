package com.hexadeventure.bootstrap.e2e.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface GameServiceTestContainer {
    static String host() {
        return GameServiceTestContainerConfig.getInstance().getServiceHost("main", 8080);
    }
    
    static Integer port() {
        return GameServiceTestContainerConfig.getInstance().getServicePort("main", 8080);
    }
    
    @DynamicPropertySource
    static void mainServiceProperties(DynamicPropertyRegistry registry) {
        registry.add("game-service.host", GameServiceTestContainer::host);
        registry.add("game-service.port", GameServiceTestContainer::port);
    }
}
