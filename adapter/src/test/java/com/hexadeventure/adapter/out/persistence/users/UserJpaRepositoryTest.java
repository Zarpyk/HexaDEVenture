package com.hexadeventure.adapter.out.persistence.users;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.hexadeventure.adapter.out.common.JPATestContainer;

@SpringBootTest
@ActiveProfiles("test-jpa")
public class UserJpaRepositoryTest extends AbstractUserRepositoryTest implements JPATestContainer {
}
