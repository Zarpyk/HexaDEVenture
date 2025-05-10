package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import com.hexadeventure.model.map.MainCharacter;

public class MainCharacterMongoMapper {
    public static MainCharacterMongoEntity toEntity(MainCharacter model) {
        MainCharacterMongoEntity mongoEntity = new MainCharacterMongoEntity();
        mongoEntity.setPosition(Vector2MongoMapper.toEntity(model.getPosition()));
        return mongoEntity;
    }
    
    public static MainCharacter toModel(MainCharacterMongoEntity entity) {
        return new MainCharacter(Vector2MongoMapper.toModel(entity.getPosition()));
    }
}
