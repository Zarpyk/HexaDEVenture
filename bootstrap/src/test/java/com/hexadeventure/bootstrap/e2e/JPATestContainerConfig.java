package com.hexadeventure.bootstrap.e2e;

import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Profile("test-jpa")
@Testcontainers
public class JPATestContainerConfig {
    
    // See: https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    
    private static final PostgreSQLContainer<?> postgreContainer = new PostgreSQLContainer<>("postgres:17-alpine");
    
    static {
        postgreContainer.start();
    }
    
    public static PostgreSQLContainer<?> getInstance() {
        return postgreContainer;
    }
}