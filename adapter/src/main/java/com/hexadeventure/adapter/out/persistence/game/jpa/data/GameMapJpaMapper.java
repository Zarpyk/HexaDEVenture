package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;

public class GameMapJpaMapper {
    public static GameMapJpaEntity toEntity(GameMap model) {
        GameMapJpaEntity entity = new GameMapJpaEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setSeed(model.getSeed());
        entity.setMapSize(model.getSize());
        /*TODO check this entity.setChunks(model.getChunks().values().stream()
                              .map(ChunkJpaMapper::toEntity)
                              .collect(Collectors.toList()));*/
        entity.setMainCharacter(MainCharacterJpaMapper.toEntity(model.getMainCharacter()));
        return entity;
    }
    
    public static GameMap toModel(GameMapJpaEntity entity) {
        /*TODO check this HashMap<Vector2, Chunk> chunks = new HashMap<>();
        for (Chunk chunk : entity.getChunks().stream()
                                 .map(ChunkJpaMapper::toModel).toList()) {
            chunks.put(chunk.getPosition(), chunk);
        }*/
        
        GameMap gameMap = new GameMap(entity.getId(),
                                      entity.getUserId(),
                                      entity.getSeed(),
                                      entity.getMapSize(),
                                      null);
        
        MainCharacterJpaEntity mainCharacter = entity.getMainCharacter();
        if(mainCharacter != null) {
            gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
        }
        
        return gameMap;
    }
}
