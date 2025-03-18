package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;

import java.util.Arrays;

public class GameMapMongoMapper {
    public static GameMapMongoEntity toEntity(GameMap model) {
        GameMapMongoEntity mongoEntity = new GameMapMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setSeed(model.getSeed());
        mongoEntity.setGridSize(model.getMapSize());
        CellDataMongoEntity[][] grid = Arrays.stream(model.getGrid())
                                             .map(row -> Arrays.stream(row)
                                                               .map(CellDataMongoMapper::toEntity)
                                                               .toArray(CellDataMongoEntity[]::new))
                                             .toArray(CellDataMongoEntity[][]::new);
        mongoEntity.setGrid(grid);
        return mongoEntity;
    }
    
    public static GameMap toModel(GameMapMongoEntity entity) {
        CellData[][] grid = Arrays.stream(entity.getGrid())
                                  .map(row -> Arrays.stream(row)
                                                    .map(CellDataMongoMapper::toModel)
                                                    .toArray(CellData[]::new))
                                  .toArray(CellData[][]::new);
        return new GameMap(entity.getId(), entity.getUserId(), entity.getSeed(), grid);
    }
}
