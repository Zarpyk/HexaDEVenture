package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;

public class Vector2MongoMapper {
    public static Vector2MongoEntity toEntity(Vector2 model) {
        Vector2MongoEntity mongoEntity = new Vector2MongoEntity();
        mongoEntity.setX(model.x);
        mongoEntity.setY(model.y);
        return mongoEntity;
    }
    
    public static Vector2MongoEntity toEntity(Vector2C model) {
        Vector2MongoEntity mongoEntity = new Vector2MongoEntity();
        mongoEntity.setX(model.x);
        mongoEntity.setY(model.y);
        return mongoEntity;
    }
    
    public static Vector2 toModel(Vector2MongoEntity entity) {
        return new Vector2(entity.getX(), entity.getY());
    }
    
    public static Vector2C toChunkModel(Vector2MongoEntity entity) {
        return new Vector2C(entity.getX(), entity.getY());
    }
}
