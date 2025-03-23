package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.adapter.out.common.MongoTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test-mongo")
public class ChunkMongoRepositoryTest extends AbstractChunkRepositoryTest implements MongoTestContainer {

}