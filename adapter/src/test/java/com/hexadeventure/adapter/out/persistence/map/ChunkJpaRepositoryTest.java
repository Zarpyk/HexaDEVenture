package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.adapter.out.common.JPATestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test-jpa")
public class ChunkJpaRepositoryTest extends AbstractChunkRepositoryTest implements JPATestContainer {
}
