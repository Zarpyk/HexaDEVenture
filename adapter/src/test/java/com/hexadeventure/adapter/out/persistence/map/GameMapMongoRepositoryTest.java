package com.hexadeventure.adapter.out.persistence.map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.hexadeventure.adapter.MongoTestContainer;

@SpringBootTest
@ActiveProfiles("test-mongo")
public class GameMapMongoRepositoryTest extends AbstractGameMapRepositoryTest implements MongoTestContainer {

}