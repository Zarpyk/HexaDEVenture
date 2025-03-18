package com.hexadeventure.adapter;

import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Profile("test-jpa")
@Testcontainers
public class JPATestContainerConfig {
    
    // See: https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    
    private static final DockerImageName mySQLImage = DockerImageName.parse("postgres:17-alpine")
                                                                     .asCompatibleSubstituteFor("mysql");
    private static final PostgreSQLContainer<?> mySQLContainer = new PostgreSQLContainer<>("postgres:17-alpine");
    
    static {
        mySQLContainer.start();
    }
    
    public static PostgreSQLContainer<?> getInstance() {
        return mySQLContainer;
    }
}