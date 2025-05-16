package com.hexadeventure.bootstrap.e2e.map;

import com.hexadeventure.bootstrap.e2e.common.MongoTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-mongo")
public class MapMongoE2ETest extends AbstractMapE2ETest implements MongoTestContainer {
}