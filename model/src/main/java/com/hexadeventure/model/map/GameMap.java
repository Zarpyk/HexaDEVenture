package com.hexadeventure.model.map;

import com.hexadeventure.model.characters.MainCharacter;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.resources.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class GameMap {
    public static final float EMPTY_THRESHOLD = 0.07f;
    
    private final String id;
    private final String userId;
    private final long seed;
    private final CellData[][] grid;
    private final Map<Vector2, Resource> resources = new HashMap<>();
    private final Map<Vector2, Enemy> enemies = new HashMap<>();
    private MainCharacter mainCharacter;
    
    @Setter
    private Vector2 bossPosition;
    
    public GameMap(String userId, long seed, int size) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.seed = seed;
        grid = new CellData[size][size];
    }
    
    public GameMap(String id, String userId, long seed, CellData[][] grid, Map<Vector2, Resource> resources,
                   Map<Vector2, Enemy> enemies) {
        this.id = id;
        this.userId = userId;
        this.seed = seed;
        this.grid = grid;
        if(resources != null) this.resources.putAll(resources);
        if(enemies != null) this.enemies.putAll(enemies);
    }
    
    public void createCell(double cellTypeThreshold, int x, int y) {
        if(cellTypeThreshold < EMPTY_THRESHOLD) {
            grid[x][y] = new CellData(new Vector2(x, y), CellType.GROUND);
        } else {
            grid[x][y] = new CellData(new Vector2(x, y), CellType.WALL);
        }
    }
    
    public CellData getCell(Vector2 position) {
        return getCell(position.x, position.y);
    }
    
    public CellData getCell(int x, int y) {
        return grid[x][y];
    }
    
    public Resource getResource(Vector2 position) {
        return resources.get(position);
    }
    
    public Enemy getEnemy(Vector2 position) {
        return enemies.get(position);
    }
    
    public void setCell(Vector2 position, CellType type) {
        grid[position.x][position.y] = new CellData(position, type);
    }
    
    public void addResource(Vector2 position, double threshold, Random random) {
        // TODO: Add more resources
        resources.put(position, new Resource(position, threshold, random));
    }
    
    public void addEnemy(Vector2 position, Enemy enemy) {
        enemies.put(position, enemy);
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
    
    /**
     * Returns the cost map of the game map.
     * @param onlyWalkable if true, non-walkable cells will have a cost of -1, otherwise a big number will be used
     * @return the cost map
     */
    public int[][] getCostMap(boolean onlyWalkable) {
        int[][] costMap = new int[grid.length][grid.length];
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid.length; y++) {
                CellType cellType = getCell(x, y).getType();
                costMap[x][y] = CellType.getCost(cellType, onlyWalkable);
            }
        }
        return costMap;
    }
}
