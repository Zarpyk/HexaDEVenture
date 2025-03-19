package com.hexadeventure.model.map;

import com.hexadeventure.model.characters.MainCharacter;
import com.hexadeventure.model.map.obstacles.ObstacleCell;
import com.hexadeventure.utils.DoubleMapper;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class GameMap {
    public static final float OBSTACLE_THRESHOLD = 0.1f;
    
    private final String id;
    private final String userId;
    private final long seed;
    private final CellData[][] grid;
    private MainCharacter mainCharacter;
    
    public GameMap(String userId, long seed, int size) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.seed = seed;
        grid = new CellData[size][size];
    }
    
    public GameMap(String id, String userId, long seed, CellData[][] grid) {
        this.id = id;
        this.userId = userId;
        this.seed = seed;
        this.grid = grid;
    }
    
    public void createCell(double cellTypeThreshold, int x, int y) {
        if(cellTypeThreshold <= OBSTACLE_THRESHOLD) {
            grid[x][y] = new ObstacleCell(new Vector2(x, y),
                                          DoubleMapper.map(cellTypeThreshold, 0, OBSTACLE_THRESHOLD, 0, 1));
        } else {
            grid[x][y] = new EmptyCell(new Vector2(x, y));
        }
    }
    
    public CellData getCell(Vector2 position) {
        return getCell(position.x, position.y);
    }
    
    public CellData getCell(int x, int y) {
        return grid[x][y];
    }
    
    public void setCell(Vector2 position, CellData cell) {
        grid[position.x][position.y] = cell;
    }
    
    public int getMapSize() {
        return grid.length;
    }
    
    public void initMainCharacter(Vector2 position) {
        mainCharacter = new MainCharacter(position);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof GameMap gameMap)) return false;
        return Objects.equals(id, gameMap.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
