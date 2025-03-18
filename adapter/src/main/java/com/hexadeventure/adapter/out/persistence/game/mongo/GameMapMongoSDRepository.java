package com.hexadeventure.adapter.out.persistence.game.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public interface GameMapMongoSDRepository extends MongoRepository<GameMapMongoEntity, String> {
}