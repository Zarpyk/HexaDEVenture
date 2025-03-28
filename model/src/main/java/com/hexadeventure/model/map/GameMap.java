package com.hexadeventure.model.map;

import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.map.resources.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class GameMap {
    private final String id;
    private final String userEmail;
    private final long seed;
    private final int size;
    @Setter
    private Map<Vector2C, Chunk> chunks = new HashMap<>();
    private final MainCharacter mainCharacter;
    private final Inventory inventory;
    
    @Setter
    private Vector2 bossPosition;
    
    public GameMap(String userEmail, long seed, int size) {
        this.id = UUID.randomUUID().toString();
        this.userEmail = userEmail;
        this.seed = seed;
        this.size = size;
        mainCharacter = new MainCharacter(new Vector2(size / 2, size / 2));
        inventory = new Inventory();
    }
    
    public GameMap(String id, String userEmail, long seed, int size, Map<Vector2C, Chunk> chunks,
                   MainCharacter mainCharacter, Inventory inventory) {
        this.id = id;
        this.userEmail = userEmail;
        this.seed = seed;
        this.size = size;
        if(chunks != null) this.chunks.putAll(chunks);
        this.mainCharacter = mainCharacter;
        this.inventory = inventory;
    }
    
    public void addChunks(Map<Vector2C, Chunk> chunks, boolean canOverrideChunks) {
        if(canOverrideChunks) {
            this.chunks.putAll(chunks);
        } else {
            for (Vector2C key : chunks.keySet()) {
                this.chunks.putIfAbsent(key, chunks.get(key));
            }
        }
    }
    
    public void setChunk(Vector2C position, Chunk chunk) {
        chunks.put(position, chunk);
    }
    
    public void createCell(double cellTypeThreshold, Vector2 position) {
        Vector2C chunkPosition = Chunk.getChunkPosition(position);
        chunks.putIfAbsent(chunkPosition, new Chunk(chunkPosition));
        chunks.get(chunkPosition).createCell(cellTypeThreshold, position);
    }
    
    public void setCell(Vector2 position, CellType type) {
        Vector2C chunkPosition = Chunk.getChunkPosition(position);
        chunks.putIfAbsent(chunkPosition, new Chunk(chunkPosition));
        chunks.get(chunkPosition).setCell(position, type);
    }
    
    
    /**
     * Returns the chunk of the given cell position.
     * @param position the position of the cell
     * @return the chunk that contains the cell
     */
    public Chunk getChunkOfCell(Vector2 position) {
        return chunks.get(Chunk.getChunkPosition(position));
    }
    
    public CellData getCell(Vector2 position) {
        return getChunkOfCell((position)).getCell(position);
    }
    
    public Resource getResource(Vector2 position) {
        return getChunkOfCell((position)).getResource(position);
    }
    
    public Enemy getEnemy(Vector2 position) {
        return getChunkOfCell((position)).getEnemy(position);
    }
    
    public void addResource(Vector2 position, double threshold, SplittableRandom random) {
        Vector2C chunkPosition = Chunk.getChunkPosition(position);
        chunks.putIfAbsent(chunkPosition, new Chunk(chunkPosition));
        chunks.get(chunkPosition).addResource(position, threshold, random);
    }
    
    public void addEnemy(Vector2 position, Enemy enemy) {
        Vector2C chunkPosition = Chunk.getChunkPosition(position);
        chunks.putIfAbsent(chunkPosition, new Chunk(chunkPosition));
        chunks.get(chunkPosition).addEnemy(position, enemy);
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
     * Returns the cost map of the given chunks.
     * @param chunkPositions the chunkPositions of the chunks
     * @param onlyWalkable if true, non-walkable cells will have a cost of -1, otherwise a big number will be used
     * @return the cost map
     */
    public Map<Vector2, Integer> getCostMap(Collection<Vector2C> chunkPositions, boolean onlyWalkable) {
        Map<Vector2, Integer> costMap = new HashMap<>();
        
        for (Vector2C position : chunkPositions) {
            int[][] chunkCostMap = chunks.get(position).getCostMap(onlyWalkable);
            for (int x = position.getRealX(); x < position.getEndX(); x++) {
                for (int y = position.getRealY(); y < position.getEndY(); y++) {
                    costMap.put(new Vector2(x, y), chunkCostMap[x - position.getRealX()][y - position.getRealY()]);
                }
            }
        }
        
        return costMap;
    }
}
