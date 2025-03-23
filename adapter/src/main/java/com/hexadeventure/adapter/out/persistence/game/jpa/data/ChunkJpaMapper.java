package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.CellDataJpaMapper;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.EnemyJpaMapper;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.ResourceJpaMapper;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChunkJpaMapper {
    public static ChunkJpaEntity toEntity(String mapId, Chunk model) {
        ChunkJpaEntity entity = new ChunkJpaEntity();
        entity.setId(model.getId());
        entity.setMapId(mapId);
        entity.setX(model.getPosition().x);
        entity.setY(model.getPosition().y);
        entity.setGrid(Arrays.stream(model.getCells())
                             .flatMap(Arrays::stream)
                             .map(CellDataJpaMapper::toEntity)
                             .collect(Collectors.toList()));
        entity.setResources(model.getResources().values().stream()
                                 .map(ResourceJpaMapper::toEntity)
                                 .collect(Collectors.toList()));
        entity.setEnemies(model.getEnemies().values().stream()
                               .map(EnemyJpaMapper::toEntity)
                               .collect(Collectors.toList()));
        return entity;
    }
    
    public static Chunk toModel(ChunkJpaEntity entity) {
        List<CellData> cellDataStream = entity.getGrid().stream()
                                              .map(CellDataJpaMapper::toModel).toList();
        CellData[][] grid = new CellData[Chunk.SIZE][Chunk.SIZE];
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
        
        return new Chunk(entity.getId(),
                         new Vector2(entity.getX(), entity.getY()),
                         grid,
                         resourceHashMap,
                         enemyHashMap);
    }
}
