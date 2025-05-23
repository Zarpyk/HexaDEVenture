package com.hexadeventure.model.map;

import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.resources.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Chunk {
    public static final int SIZE = 16;
    
    private final String id;
    private final Vector2C position;
    
    private final CellData[][] cells;
    
    // Vector2 is the real position on the map (is not the chunk position with 0-index)
    private final Map<Vector2, Resource> resources;
    private final Map<Vector2, Enemy> enemies;
    
    public Chunk(Vector2C position) {
        this.id = UUID.randomUUID().toString();
        this.position = position;
        this.cells = new CellData[SIZE][SIZE];
        this.resources = new HashMap<>();
        this.enemies = new HashMap<>();
    }
    
    public static Vector2C getChunkPosition(Vector2 position) {
        return getChunkPosition(position.x, position.y);
    }
    
    public static Vector2C getChunkPosition(int x, int y) {
        return new Vector2C(x / SIZE, y / SIZE);
    }
    
    public void createCell(double cellTypeThreshold, Vector2 position) {
        int x = position.x % SIZE;
        int y = position.y % SIZE;
        cells[x][y] = new CellData(position, cellTypeThreshold);
    }
    
    public void setCell(Vector2 position, CellType type) {
        int x = position.x % SIZE;
        int y = position.y % SIZE;
        cells[x][y] = new CellData(position, type);
    }
    
    public CellData getCell(Vector2 position) {
        int x = position.x % SIZE;
        int y = position.y % SIZE;
        return cells[x][y];
    }
    
    public Resource getResource(Vector2 position) {
        return resources.get(position);
    }
    
    public Enemy getEnemy(Vector2 position) {
        return enemies.get(position);
    }
    
    public void addResource(Vector2 position, double threshold, SplittableRandom random) {
        resources.put(position, new Resource(position, threshold, random));
    }
    
    public void addEnemy(Vector2 position, Enemy enemy) {
        enemy.setPosition(position);
        enemies.put(position, enemy);
    }
    
    public void removeResource(Vector2 position) {
        resources.remove(position);
    }
    
    public void removeEnemy(Vector2 position) {
        enemies.remove(position);
    }
    
    /**
     * Returns the cost map of the chunk.
     * @param onlyWalkable if true, non-walkable cells will have a cost of -1, otherwise a big number will be used
     * @param ignoreEnemies if true, enemies will be -1
     * @param notIgnoreEnemy if not null, the enemy on this position will not be -1
     * @return the cost map
     */
    public int[][] getCostMap(boolean onlyWalkable, boolean ignoreEnemies, Vector2 notIgnoreEnemy) {
        int[][] costMap = new int[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                CellType cellType = getCell(new Vector2(x, y)).getType();
                costMap[x][y] = CellType.getCost(cellType, onlyWalkable);
                
                if(ignoreEnemies) {
                    Vector2 enemyPosition = new Vector2(x, y).add(position.x * SIZE, position.y * SIZE);
                    if(!enemyPosition.equals(notIgnoreEnemy) && enemies.containsKey(enemyPosition)) {
                        costMap[x][y] = -1;
                    }
                }
            }
        }
        return costMap;
    }
}
