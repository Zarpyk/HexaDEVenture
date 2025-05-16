package com.hexadeventure.bootstrap.e2e.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface MongoTestContainer {
    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MongoDBTestContainerConfig.getInstance().getReplicaSetUrl());
    }
}
