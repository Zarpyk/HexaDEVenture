package com.hexadeventure.bootstrap.e2e.game;

import com.hexadeventure.bootstrap.e2e.common.JPATestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-jpa")
public class GameJpaE2ETest extends AbstractGameE2ETest implements JPATestContainer {
}
