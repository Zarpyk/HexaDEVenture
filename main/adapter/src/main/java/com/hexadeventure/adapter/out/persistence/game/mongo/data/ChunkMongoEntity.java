package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Chunks")
@Getter
@Setter
public class ChunkMongoEntity {
    @MongoId
    private String id;
    private String mapId;
    private Vector2MongoEntity position;
    
    private String cellsFileId;
    private String resourcesFileId;
    private String enemiesFileId;
}
