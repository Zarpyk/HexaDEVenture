package com.hexadeventure.adapter.out.persistence.game.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "GameMaps")
@Getter
@Setter
public class GameMapMongoEntity {
    @MongoId
    private String id;
    private String userId;
    private long seed;
    private int gridSize;
    private String gridFileId;
    private String resourcesFileId;
    private String enemiesFileId;
    private MainCharacterMongoEntity mainCharacter;
}
