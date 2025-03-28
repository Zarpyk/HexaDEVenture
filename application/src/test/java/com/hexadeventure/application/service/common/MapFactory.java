package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.map.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MapFactory {
    public static final String TEST_USER_EMAIL = "test@test.com";
    public static final long TEST_SEED = 1234;
    
    public static final String EMPTY_MAP_ID = UUID.randomUUID().toString();
    public static final int EMPTY_MAP_SIZE_MULTIPLIER = 2;
    public static final int EMPTY_MAP_SIZE = GameService.MIN_MAP_SIZE * EMPTY_MAP_SIZE_MULTIPLIER;
    public static final Vector2 EMPTY_START_POSITION = new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2);
    private static final int EMPTY_MAP_MOVE_COUNT = 16;
    public static final Vector2 EMPTY_END_POSITION = new Vector2(EMPTY_MAP_SIZE / 2 + EMPTY_MAP_MOVE_COUNT,
                                                                 EMPTY_MAP_SIZE / 2 + EMPTY_MAP_MOVE_COUNT);
    public static final int EMPTY_MAP_PATH_LENGTH = EMPTY_MAP_MOVE_COUNT + EMPTY_MAP_MOVE_COUNT - 1;
    
    public static final String OBSTACLE_MAP_ID = UUID.randomUUID().toString();
    public static final int OBSTACLE_MAP_SIZE_MULTIPLIER = 2;
    public static final int OBSTACLE_MAP_SIZE = GameService.MIN_MAP_SIZE * OBSTACLE_MAP_SIZE_MULTIPLIER;
    public static final Vector2 OBSTACLE_START_POSITION = new Vector2(OBSTACLE_MAP_SIZE / 2, OBSTACLE_MAP_SIZE / 2);
    private static final int OBSTACLE_MAP_MOVE_COUNT = 16;
    public static final Vector2 OBSTACLE_END_POSITION = new Vector2(OBSTACLE_MAP_SIZE / 2 + OBSTACLE_MAP_MOVE_COUNT,
                                                                    OBSTACLE_MAP_SIZE / 2 + OBSTACLE_MAP_MOVE_COUNT);
    
    public static final String RESOURCE_MAP_ID = UUID.randomUUID().toString();
    public static final int RESOURCE_MAP_SIZE_MULTIPLIER = 2;
    public static final int RESOURCE_MAP_SIZE = GameService.MIN_MAP_SIZE * EMPTY_MAP_SIZE_MULTIPLIER;
    public static final Vector2 RESOURCE_START_POSITION = new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2);
    private static final int RESOURCE_MAP_MOVE_COUNT = 16;
    public static final Vector2 RESOURCE_END_POSITION = new Vector2(EMPTY_MAP_SIZE / 2 + RESOURCE_MAP_MOVE_COUNT,
                                                                    EMPTY_MAP_SIZE / 2 + RESOURCE_MAP_MOVE_COUNT);
    public static final int RESOURCE_MAP_PATH_LENGTH = RESOURCE_MAP_MOVE_COUNT + RESOURCE_MAP_MOVE_COUNT - 1;
    
    
    public static void createEmptyGameMap(GameMapRepository gameMapRepository, ChunkRepository chunkRepository,
                                          AStarPathfinder aStarPathfinder) {
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = EMPTY_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        centerChunk.getArroundPositions(EMPTY_MAP_SIZE_MULTIPLIER, true).forEach(chunkPosition -> {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = 0; x < Chunk.SIZE; x++) {
                for (int y = 0; y < Chunk.SIZE; y++) {
                    chunk.setCell(new Vector2(x, y), CellType.GROUND);
                }
            }
        });
        mockChunk(chunkRepository, EMPTY_MAP_ID, chunks);
        
        GameMap gameMap = new GameMap(EMPTY_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      EMPTY_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2)),
                                      new Inventory());
        
        Map<Vector2, Integer> costMap = gameMap.getCostMap(centerChunk.getArroundPositions(1, true),
                                                           true);
        
        Queue<Vector2> path = generatePath(EMPTY_START_POSITION, EMPTY_MAP_PATH_LENGTH, EMPTY_END_POSITION);
        
        when(gameMapRepository.findById(eq(EMPTY_MAP_ID))).thenReturn(java.util.Optional.of(gameMap));
        when(aStarPathfinder.generatePath(any(), any(), eq(costMap))).thenReturn(path);
    }
    
    public static void createObstacleGameMap(GameMapRepository gameMapRepository, ChunkRepository chunkRepository,
                                             AStarPathfinder aStarPathfinder) {
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = OBSTACLE_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        centerChunk.getArroundPositions(OBSTACLE_MAP_SIZE_MULTIPLIER, true).forEach(chunkPosition -> {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = 0; x < Chunk.SIZE; x++) {
                for (int y = 0; y < Chunk.SIZE; y++) {
                    chunk.setCell(new Vector2(x, y), CellType.WALL);
                }
            }
        });
        mockChunk(chunkRepository, OBSTACLE_MAP_ID, chunks);
        
        GameMap gameMap = new GameMap(OBSTACLE_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      OBSTACLE_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2)),
                                      new Inventory());
        
        when(gameMapRepository.findById(eq(OBSTACLE_MAP_ID))).thenReturn(java.util.Optional.of(gameMap));
    }
    
    public static GameMap createResourceGameMap(GameMapRepository gameMapRepository, ChunkRepository chunkRepository,
                                                AStarPathfinder aStarPathfinder) {
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = RESOURCE_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        centerChunk.getArroundPositions(RESOURCE_MAP_SIZE_MULTIPLIER, true).forEach(chunkPosition -> {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = 0; x < Chunk.SIZE; x++) {
                for (int y = 0; y < Chunk.SIZE; y++) {
                    Vector2 position = new Vector2(x, y);
                    chunk.setCell(position, CellType.GROUND);
                    Vector2 resourcePosition = new Vector2(chunk.getPosition().x * Chunk.SIZE + position.x,
                                                           chunk.getPosition().y * Chunk.SIZE + position.y);
                    if(!resourcePosition.equals(RESOURCE_START_POSITION)) {
                        chunk.addResource(resourcePosition,
                                          0,
                                          new SplittableRandom(position.getRandomSeed(TEST_SEED, 0)));
                    }
                }
            }
        });
        mockChunk(chunkRepository, RESOURCE_MAP_ID, chunks);
        
        GameMap gameMap = new GameMap(RESOURCE_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      RESOURCE_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(RESOURCE_MAP_SIZE / 2, RESOURCE_MAP_SIZE / 2)),
                                      new Inventory());
        
        Map<Vector2, Integer> costMap = gameMap.getCostMap(centerChunk.getArroundPositions(1, true),
                                                           true);
        
        Queue<Vector2> path = generatePath(RESOURCE_START_POSITION, RESOURCE_MAP_PATH_LENGTH, RESOURCE_END_POSITION);
        
        when(gameMapRepository.findById(eq(RESOURCE_MAP_ID))).thenReturn(java.util.Optional.of(gameMap));
        when(aStarPathfinder.generatePath(any(), any(), eq(costMap))).thenReturn(path);
        return gameMap;
    }
    
    private static Queue<Vector2> generatePath(Vector2 startPosition, int pathLength, Vector2 endPosition) {
        Queue<Vector2> path = new LinkedList<>();
        path.add(startPosition);
        for (int x = 1; x <= (pathLength - 2) / 2; x++) {
            path.add(startPosition.add(x, 0));
        }
        for (int y = 1; y <= (pathLength - 2) / 2; y++) {
            path.add(startPosition.add((pathLength - 2) / 2, y));
        }
        if(pathLength % 2 != 0) {
            Vector2 lastAddedPosition = startPosition.add((pathLength - 2) / 2,
                                                          (pathLength - 2) / 2);
            Vector2 position = endPosition.subtract(0, 1);
            if(position.equals(lastAddedPosition)) {
                path.add(endPosition.subtract(1, 0));
            } else {
                path.add(position);
            }
        }
        path.add(endPosition);
        return path;
    }
    
    private static void mockChunk(ChunkRepository chunkRepository, String obstacleMapId, Map<Vector2C, Chunk> chunks) {
        when(chunkRepository.findChunks(eq(obstacleMapId), any())).thenAnswer(x -> {
            Collection<Vector2C> positions = x.getArgument(1);
            Map<Vector2C, Chunk> result = new HashMap<>();
            positions.forEach(position -> result.put(position, chunks.get(position)));
            return result;
        });
    }
}
