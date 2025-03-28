package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.model.characters.MainCharacter;
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
        
        Queue<Vector2> path = new LinkedList<>();
        path.add(EMPTY_START_POSITION);
        for (int i = 0; i < EMPTY_MAP_PATH_LENGTH - 2; i++) {
            path.add(new Vector2(0, 0));
        }
        path.add(EMPTY_END_POSITION);
        
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
    
    private static void mockChunk(ChunkRepository chunkRepository, String obstacleMapId, Map<Vector2C, Chunk> chunks) {
        when(chunkRepository.findChunks(eq(obstacleMapId), any())).thenAnswer(x -> {
            Collection<Vector2C> positions = x.getArgument(1);
            Map<Vector2C, Chunk> result = new HashMap<>();
            positions.forEach(position -> result.put(position, chunks.get(position)));
            return result;
        });
    }
}
