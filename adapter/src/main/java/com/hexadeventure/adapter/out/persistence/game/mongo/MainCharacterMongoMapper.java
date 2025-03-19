package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.model.characters.MainCharacter;
import com.hexadeventure.model.map.Vector2;

public class MainCharacterMongoMapper {
    public static MainCharacterMongoEntity toEntity(MainCharacter model) {
        MainCharacterMongoEntity mongoEntity = new MainCharacterMongoEntity();
        mongoEntity.setX(model.getPosition().x);
        mongoEntity.setY(model.getPosition().y);
        return mongoEntity;
    }
    
    public static MainCharacter toModel(MainCharacterMongoEntity entity) {
        return new MainCharacter(new Vector2(entity.getX(), entity.getY()));
    }
}
