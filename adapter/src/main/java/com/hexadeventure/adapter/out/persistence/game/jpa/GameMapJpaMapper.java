package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameMapJpaMapper {
    public static GameMapJpaEntity toEntity(GameMap model) {
        GameMapJpaEntity entity = new GameMapJpaEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setSeed(model.getSeed());
        entity.setGridSize(model.getMapSize());
        entity.setGrid(Arrays.stream(model.getGrid())
                             .flatMap(Arrays::stream)
                             .map(CellDataJpaMapper::toEntity)
                             .collect(Collectors.toList()));
        entity.setMainCharacter(MainCharacterJpaMapper.toEntity(model.getMainCharacter()));
        return entity;
    }
    
    public static GameMap toModel(GameMapJpaEntity entity) {
        List<CellData> cellDataStream = entity.getGrid().stream()
                                              .map(CellDataJpaMapper::toModel).toList();
        CellData[][] grid = new CellData[entity.getGridSize()][entity.getGridSize()];
        for (CellData cellData : cellDataStream) {
            grid[cellData.getPosition().x][cellData.getPosition().y] = cellData;
        }
        
        GameMap gameMap = new GameMap(entity.getId(), entity.getUserId(), entity.getSeed(), grid);
        
        MainCharacterJpaEntity mainCharacter = entity.getMainCharacter();
        if(mainCharacter != null) {
            gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
        }
        
        return gameMap;
    }
}
