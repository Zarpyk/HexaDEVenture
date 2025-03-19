package com.hexadeventure.bootstrap.e2e.map;

import com.hexadeventure.bootstrap.e2e.JPATestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-jpa")
public class MapJpaE2ETest extends AbstractMapE2ETest implements JPATestContainer {
}
