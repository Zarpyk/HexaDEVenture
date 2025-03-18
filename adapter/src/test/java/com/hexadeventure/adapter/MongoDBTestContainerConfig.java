package com.hexadeventure.adapter;

import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Launcher for the application: starts the Spring application.
 *
 * @author Alfredo Rueda Unsain
 * @see
 * <a href="https://github.com/alfredorueda/hexagonal-architecture-java-mysql-mongodb/blob/093b95950581b6c79817b39e36db862a7e02f288/adapter/src/test/java/eu/happycoders/shop/adapter/out/persistence/mongo/MongoDBTestContainerConfig.java">MongoDBTestContainerConfig.java</a>
 */
@Profile("test-mongo")
@Testcontainers
public class MongoDBTestContainerConfig {
    
    // See: https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.4");
    
    static {
        mongoDBContainer.start();
    }
    
    public static MongoDBContainer getInstance() {
        return mongoDBContainer;
    }
}