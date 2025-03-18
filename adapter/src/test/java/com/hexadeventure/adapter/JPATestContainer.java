package com.hexadeventure.adapter;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface JPATestContainer {
    @DynamicPropertySource
    static void jpaDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> JPATestContainerConfig.getInstance().getJdbcUrl());
        registry.add("spring.datasource.username", () -> JPATestContainerConfig.getInstance().getUsername());
        registry.add("spring.datasource.password", () -> JPATestContainerConfig.getInstance().getPassword());
    }
}
