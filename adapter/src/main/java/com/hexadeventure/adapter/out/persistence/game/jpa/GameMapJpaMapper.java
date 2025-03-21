package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.CellDataJpaMapper;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.EnemyJpaMapper;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.ResourceJpaMapper;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;

import java.util.Arrays;
import java.util.HashMap;
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
        entity.setResources(model.getResources().values().stream()
                                 .map(ResourceJpaMapper::toEntity)
                                 .collect(Collectors.toList()));
        entity.setEnemies(model.getEnemies().values().stream()
                               .map(EnemyJpaMapper::toEntity)
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
        
        HashMap<Vector2, Resource> resourceHashMap = new HashMap<>();
        for (Resource resource : entity.getResources().stream()
                                       .map(ResourceJpaMapper::toModel).toList()) {
            resourceHashMap.put(resource.getPosition(), resource);
        }
        
        HashMap<Vector2, Enemy> enemyHashMap = new HashMap<>();
        for (Enemy enemy : entity.getEnemies().stream()
                                 .map(EnemyJpaMapper::toModel).toList()) {
            enemyHashMap.put(enemy.getPosition(), enemy);
        }
        
        GameMap gameMap = new GameMap(entity.getId(), entity.getUserId(), entity.getSeed(), grid,
                                      resourceHashMap, enemyHashMap);
        
        MainCharacterJpaEntity mainCharacter = entity.getMainCharacter();
        if(mainCharacter != null) {
            gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
        }
        
        return gameMap;
    }
}
