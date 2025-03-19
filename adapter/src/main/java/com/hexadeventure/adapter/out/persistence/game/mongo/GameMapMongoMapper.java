package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;

public class GameMapMongoMapper {
    public static GameMapMongoEntity toEntity(GameMap model) {
        GameMapMongoEntity mongoEntity = new GameMapMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setSeed(model.getSeed());
        mongoEntity.setGridSize(model.getMapSize());
        mongoEntity.setMainCharacter(MainCharacterMongoMapper.toEntity(model.getMainCharacter()));
        return mongoEntity;
    }
    
    public static GameMap toModel(GameMapMongoEntity entity, CellData[][] grid) {
        GameMap gameMap = new GameMap(entity.getId(), entity.getUserId(), entity.getSeed(), grid);
        MainCharacterMongoEntity mainCharacter = entity.getMainCharacter();
        if(mainCharacter != null) {
            gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
        }
        return gameMap;
    }
}
