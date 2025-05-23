package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.adapter.out.persistence.game.mongo.data.ChunkMongoEntity;
import com.hexadeventure.adapter.out.persistence.game.mongo.data.Vector2MongoEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public interface ChunkMongoSDRepository extends MongoRepository<ChunkMongoEntity, String> {
    boolean existsByMapId(String mapId);
    Optional<ChunkMongoEntity> findByMapIdAndPosition(String mapId, Vector2MongoEntity position);
    void deleteByMapId(String mapId);
}