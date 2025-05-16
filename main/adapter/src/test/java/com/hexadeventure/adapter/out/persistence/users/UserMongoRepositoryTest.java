package com.hexadeventure.adapter.out.persistence.users;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.hexadeventure.adapter.out.common.MongoTestContainer;

@SpringBootTest
@ActiveProfiles("test-mongo")
public class UserMongoRepositoryTest extends AbstractUserRepositoryTest implements MongoTestContainer {
}
