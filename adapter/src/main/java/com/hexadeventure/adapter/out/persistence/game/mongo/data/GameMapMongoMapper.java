package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;

public class GameMapMongoMapper {
    public static GameMapMongoEntity toEntity(GameMap model) {
        GameMapMongoEntity mongoEntity = new GameMapMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setUserId(model.getUserEmail());
        mongoEntity.setSeed(model.getSeed());
        mongoEntity.setMapSize(model.getSize());
        // TODO store chunk?
        mongoEntity.setMainCharacter(MainCharacterMongoMapper.toEntity(model.getMainCharacter()));
        return mongoEntity;
    }
    
    public static GameMap toModel(GameMapMongoEntity entity) {
        // TODO null chunks
        GameMap gameMap = new GameMap(entity.getId(),
                                      entity.getUserId(),
                                      entity.getSeed(),
                                      entity.getMapSize(),
                                      null);
        
        MainCharacterMongoEntity mainCharacter = entity.getMainCharacter();
        if(mainCharacter != null) {
            gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
        }
        
        return gameMap;
    }
}
