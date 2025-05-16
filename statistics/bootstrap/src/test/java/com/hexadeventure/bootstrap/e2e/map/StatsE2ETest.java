package com.hexadeventure.bootstrap.e2e.map;

import com.hexadeventure.bootstrap.e2e.common.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatsE2ETest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    public void beforeEach() {
        UserFactory.createTestUser(port);
    }
    
    @AfterEach
    public void afterEach() {
        UserFactory.deleteTestUser(port);
    }
}