package com.hexadeventure.adapter.out.persistence.game.mongo.data;

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
    private int mapSize;
    private MainCharacterMongoEntity mainCharacter;
    
    private String inventoryFileId;
    private String combatTerrainFileId;
    
    private Vector2MongoEntity bossPosition;
    private boolean isInCombat;
    private boolean isBossBattle;
}
