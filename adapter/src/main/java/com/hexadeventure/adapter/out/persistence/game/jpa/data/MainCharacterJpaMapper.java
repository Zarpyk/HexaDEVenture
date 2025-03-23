package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.model.characters.MainCharacter;
import com.hexadeventure.model.map.Vector2;

public class MainCharacterJpaMapper {
    public static MainCharacterJpaEntity toEntity(MainCharacter model) {
        MainCharacterJpaEntity mongoEntity = new MainCharacterJpaEntity();
        mongoEntity.setX(model.getPosition().x);
        mongoEntity.setY(model.getPosition().y);
        return mongoEntity;
    }
    
    public static MainCharacter toModel(MainCharacterJpaEntity entity) {
        return new MainCharacter(new Vector2(entity.getX(), entity.getY()));
    }
}
