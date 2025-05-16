package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class MapFactory {
    public static final String TEST_USER_EMAIL = "test@test.com";
    public static final long TEST_SEED = 1234;
    
    public static final String EMPTY_MAP_ID = UUID.randomUUID().toString();
    public static final int EMPTY_MAP_SIZE_MULTIPLIER = 2;
    public static final int EMPTY_MAP_SIZE = GameService.MIN_MAP_SIZE * EMPTY_MAP_SIZE_MULTIPLIER;
    public static final Vector2 EMPTY_PLAYER_START_POSITION = new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2);
    private static final int EMPTY_MAP_MOVE_COUNT = 16;
    public static final Vector2 EMPTY_END_POSITION = new Vector2(EMPTY_MAP_SIZE / 2 + EMPTY_MAP_MOVE_COUNT,
                                                                 EMPTY_MAP_SIZE / 2 + EMPTY_MAP_MOVE_COUNT);
    public static final int EMPTY_MAP_PATH_LENGTH = EMPTY_MAP_MOVE_COUNT + EMPTY_MAP_MOVE_COUNT;
    
    public static final String OBSTACLE_MAP_ID = UUID.randomUUID().toString();
    public static final int OBSTACLE_MAP_SIZE_MULTIPLIER = 2;
    public static final int OBSTACLE_MAP_SIZE = GameService.MIN_MAP_SIZE * OBSTACLE_MAP_SIZE_MULTIPLIER;
    public static final Vector2 OBSTACLE_PLAYER_START_POSITION = new Vector2(OBSTACLE_MAP_SIZE / 2,
                                                                             OBSTACLE_MAP_SIZE / 2);
    private static final int OBSTACLE_MAP_MOVE_COUNT = 16;
    public static final Vector2 OBSTACLE_END_POSITION = new Vector2(OBSTACLE_MAP_SIZE / 2 + OBSTACLE_MAP_MOVE_COUNT,
                                                                    OBSTACLE_MAP_SIZE / 2 + OBSTACLE_MAP_MOVE_COUNT);
    
    public static final String RESOURCE_MAP_ID = UUID.randomUUID().toString();
    public static final int RESOURCE_MAP_SIZE_MULTIPLIER = 2;
    public static final int RESOURCE_MAP_SIZE = GameService.MIN_MAP_SIZE * RESOURCE_MAP_SIZE_MULTIPLIER;
    public static final Vector2 RESOURCE_PLAYER_START_POSITION = new Vector2(RESOURCE_MAP_SIZE / 2,
                                                                             RESOURCE_MAP_SIZE / 2);
    private static final int RESOURCE_MAP_MOVE_COUNT = 16;
    public static final Vector2 RESOURCE_END_POSITION = new Vector2(RESOURCE_MAP_SIZE / 2 + RESOURCE_MAP_MOVE_COUNT,
                                                                    RESOURCE_MAP_SIZE / 2 + RESOURCE_MAP_MOVE_COUNT);
    public static final int RESOURCE_MAP_PATH_LENGTH = RESOURCE_MAP_MOVE_COUNT + RESOURCE_MAP_MOVE_COUNT;
    
    public static final String ENEMY_MAP_ID = UUID.randomUUID().toString();
    public static final int ENEMY_MAP_SIZE_MULTIPLIER = 2;
    public static final int ENEMY_MAP_SIZE = GameService.MIN_MAP_SIZE * ENEMY_MAP_SIZE_MULTIPLIER;
    public static final Vector2 ENEMY_PLAYER_START_POSITION = new Vector2(ENEMY_MAP_SIZE / 2, ENEMY_MAP_SIZE / 2);
    private static final int ENEMY_MAP_MOVE_COUNT = 16;
    public static final Vector2 ENEMY_END_POSITION = new Vector2(ENEMY_MAP_SIZE / 2 + ENEMY_MAP_MOVE_COUNT,
                                                                 ENEMY_MAP_SIZE / 2 + ENEMY_MAP_MOVE_COUNT);
    public static final int ENEMY_MAP_PATH_LENGTH = ENEMY_MAP_MOVE_COUNT + ENEMY_MAP_MOVE_COUNT;
    public static final int ENEMY_MAP_ENEMY_OFFSET = 16;
    public static final Vector2 ENEMY_MAP_ENEMY_POSITION = new Vector2(ENEMY_MAP_SIZE / 2 - ENEMY_MAP_ENEMY_OFFSET,
                                                                       ENEMY_MAP_SIZE / 2);
    
    public static final int COMBAT_ROW_SIZE = 3;
    public static final int COMBAT_COLUMN_SIZE = 4;
    
    
    public static GameMap createEmptyGameMap(GameMapRepository gameMapRepository, AStarPathfinder aStarPathfinder,
                                             SettingsImporter settingsImporter) {
        // Generate chunks
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = EMPTY_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        for (Vector2C chunkPosition : centerChunk.getAroundPositions(EMPTY_MAP_SIZE_MULTIPLIER, true)) {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = chunkPosition.getRealX(); x < chunkPosition.getEndX(); x++) {
                for (int y = chunkPosition.getRealY(); y < chunkPosition.getEndY(); y++) {
                    chunk.setCell(new Vector2(x, y), CellType.GROUND);
                }
            }
        }
        mockChunk(gameMapRepository, EMPTY_MAP_ID, chunks);
        
        // Create the game map
        GameMap gameMap = new GameMap(EMPTY_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      EMPTY_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2)),
                                      new Inventory(),
                                      new CombatTerrain(COMBAT_ROW_SIZE, COMBAT_COLUMN_SIZE),
                                      new Vector2(0, 0),
                                      false,
                                      false);
        
        Map<Vector2, Integer> costMap = gameMap.getCostMap(centerChunk.getAroundPositions(1, true),
                                                           true);
        gameMap.setChunks(new HashMap<>());
        
        // Generate the path
        Queue<Vector2> path = generatePath(EMPTY_PLAYER_START_POSITION, EMPTY_END_POSITION);
        
        mockGameMap(gameMapRepository, EMPTY_MAP_ID, gameMap, chunks);
        when(aStarPathfinder.generatePath(any(), any(), eq(costMap))).thenReturn(path);
        ItemFactory.setupSettingsImporter(settingsImporter);
        return gameMap;
    }
    
    public static void createObstacleGameMap(GameMapRepository gameMapRepository, SettingsImporter settingsImporter) {
        // Generate chunks
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = OBSTACLE_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        for (Vector2C chunkPosition : centerChunk.getAroundPositions(OBSTACLE_MAP_SIZE_MULTIPLIER, true)) {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = chunkPosition.getRealX(); x < chunkPosition.getEndX(); x++) {
                for (int y = chunkPosition.getRealY(); y < chunkPosition.getEndY(); y++) {
                    // Add an obstacle
                    chunk.setCell(new Vector2(x, y), CellType.WALL);
                }
            }
        }
        mockChunk(gameMapRepository, OBSTACLE_MAP_ID, chunks);
        
        // Create the game map
        GameMap gameMap = new GameMap(OBSTACLE_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      OBSTACLE_MAP_SIZE,
                                      null,
                                      new MainCharacter(new Vector2(EMPTY_MAP_SIZE / 2, EMPTY_MAP_SIZE / 2)),
                                      new Inventory(),
                                      new CombatTerrain(COMBAT_ROW_SIZE, COMBAT_COLUMN_SIZE),
                                      new Vector2(0, 0),
                                      false,
                                      false);
        
        mockGameMap(gameMapRepository, OBSTACLE_MAP_ID, gameMap, chunks);
        ItemFactory.setupSettingsImporter(settingsImporter);
    }
    
    public static GameMap createResourceGameMap(GameMapRepository gameMapRepository, AStarPathfinder aStarPathfinder,
                                                SettingsImporter settingsImporter, boolean returnChunks) {
        // Generate chunks
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = RESOURCE_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        for (Vector2C chunkPosition : centerChunk.getAroundPositions(RESOURCE_MAP_SIZE_MULTIPLIER, true)) {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = chunkPosition.getRealX(); x < chunkPosition.getEndX(); x++) {
                for (int y = chunkPosition.getRealY(); y < chunkPosition.getEndY(); y++) {
                    Vector2 position = new Vector2(x, y);
                    chunk.setCell(position, CellType.GROUND);
                    // Add resource
                    if(!position.equals(RESOURCE_PLAYER_START_POSITION)) {
                        chunk.addResource(position,
                                          0,
                                          new SplittableRandom(position.getRandomSeed(TEST_SEED, 0)));
                    }
                }
            }
        }
        mockChunk(gameMapRepository, RESOURCE_MAP_ID, chunks);
        
        // Create the game map
        GameMap gameMap = new GameMap(RESOURCE_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      RESOURCE_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(RESOURCE_MAP_SIZE / 2, RESOURCE_MAP_SIZE / 2)),
                                      new Inventory(),
                                      new CombatTerrain(COMBAT_ROW_SIZE, COMBAT_COLUMN_SIZE),
                                      new Vector2(0, 0),
                                      false,
                                      false);
        
        Map<Vector2, Integer> costMap = gameMap.getCostMap(centerChunk.getAroundPositions(1, true),
                                                           true);
        if(!returnChunks) gameMap.setChunks(new HashMap<>());
        
        // Generate a path for player
        Queue<Vector2> path = generatePath(RESOURCE_PLAYER_START_POSITION, RESOURCE_END_POSITION);
        
        mockGameMap(gameMapRepository, RESOURCE_MAP_ID, gameMap, chunks);
        when(aStarPathfinder.generatePath(any(), any(), eq(costMap))).thenReturn(path);
        ItemFactory.setupSettingsImporter(settingsImporter);
        return gameMap;
    }
    
    public static GameMap createEnemyGameMap(GameMapRepository gameMapRepository, AStarPathfinder aStarPathfinder,
                                             SettingsImporter settingsImporter) {
        // Generate chunks
        Map<Vector2C, Chunk> chunks = new HashMap<>();
        int center = ENEMY_MAP_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        for (Vector2C chunkPosition : centerChunk.getAroundPositions(ENEMY_MAP_SIZE_MULTIPLIER, true)) {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
            Chunk chunk = chunks.get(chunkPosition);
            for (int x = chunkPosition.getRealX(); x < chunkPosition.getEndX(); x++) {
                for (int y = chunkPosition.getRealY(); y < chunkPosition.getEndY(); y++) {
                    Vector2 position = new Vector2(x, y);
                    chunk.setCell(position, CellType.GROUND);
                    // Add enemy
                    if(position.equals(ENEMY_MAP_ENEMY_POSITION)) {
                        SplittableRandom random = new SplittableRandom(position.getRandomSeed(TEST_SEED, 0));
                        EnemyPattern pattern = EnemyFactory.createEnemyPattern();
                        Map<WeaponType, List<WeaponSetting>> weapons = WeaponFactory.createWeaponsSettings();
                        chunk.addEnemy(new Vector2(x, y), new Enemy(position, random, pattern, weapons));
                    }
                }
            }
        }
        mockChunk(gameMapRepository, ENEMY_MAP_ID, chunks);
        
        // Create the game map
        GameMap gameMap = new GameMap(ENEMY_MAP_ID,
                                      TEST_USER_EMAIL,
                                      TEST_SEED,
                                      ENEMY_MAP_SIZE,
                                      chunks,
                                      new MainCharacter(new Vector2(ENEMY_MAP_SIZE / 2, ENEMY_MAP_SIZE / 2)),
                                      new Inventory(),
                                      new CombatTerrain(COMBAT_ROW_SIZE, COMBAT_COLUMN_SIZE),
                                      new Vector2(0, 0),
                                      false,
                                      false);
        
        Map<Vector2, Integer> costMap = gameMap.getCostMap(centerChunk.getAroundPositions(1, true),
                                                           true, true, ENEMY_MAP_ENEMY_POSITION);
        gameMap.setChunks(new HashMap<>());
        
        // Generate a path for the main character
        Queue<Vector2> path = generatePath(ENEMY_PLAYER_START_POSITION, ENEMY_END_POSITION);
        
        // Generate a path for the enemy
        Queue<Vector2> lastPath;
        Vector2 enemyPosition = ENEMY_MAP_ENEMY_POSITION;
        for (Vector2 position : path) {
            if (position == ENEMY_PLAYER_START_POSITION) continue;
            lastPath = generatePath(enemyPosition, position);
            when(aStarPathfinder.generatePath(eq(enemyPosition), eq(position), anyMap())).thenReturn(lastPath);
            Queue<Vector2> temp = new LinkedList<>(lastPath);
            // Ignore the first position
            temp.poll();
            for (int j = 0; j < Enemy.MOVEMENT_SPEED; j++) {
                if(temp.isEmpty()) break;
                enemyPosition = temp.poll();
            }
        }
        
        mockGameMap(gameMapRepository, ENEMY_MAP_ID, gameMap, chunks);
        when(aStarPathfinder.generatePath(eq(ENEMY_PLAYER_START_POSITION), eq(ENEMY_END_POSITION), anyMap()))
                .thenReturn(path);
        ItemFactory.setupSettingsImporter(settingsImporter);
        return gameMap;
    }
    
    /**
     * Generate a path from start position to end position with a given length.
     * @param startPosition start position
     * @param endPosition end position
     * @return the generated path
     */
    private static Queue<Vector2> generatePath(Vector2 startPosition, Vector2 endPosition) {
        Queue<Vector2> path = new LinkedList<>();
        path.add(startPosition);
        Vector2 lastPosition = startPosition;
        while (!lastPosition.equals(endPosition)) {
            if(lastPosition.x < endPosition.x) {
                lastPosition = lastPosition.add(1, 0);
            } else if(lastPosition.x > endPosition.x) {
                lastPosition = lastPosition.add(-1, 0);
            } else if(lastPosition.y < endPosition.y) {
                lastPosition = lastPosition.add(0, 1);
            } else if(lastPosition.y > endPosition.y) {
                lastPosition = lastPosition.add(0, -1);
            }
            path.add(lastPosition);
        }
        return path;
    }
    
    /**
     * Mock the chunk repository to return the chunks at the given positions.
     * @param gameMapRepository the game map repository
     * @param mapId the map id
     * @param chunks the chunks to return
     */
    private static void mockChunk(GameMapRepository gameMapRepository, String mapId, Map<Vector2C, Chunk> chunks) {
        when(gameMapRepository.findMapChunks(eq(mapId), any())).thenAnswer(x -> {
            Collection<Vector2C> positions = x.getArgument(1);
            Map<Vector2C, Chunk> result = new HashMap<>();
            positions.forEach(position -> result.put(position, chunks.get(position)));
            return result;
        });
    }
    
    
    /**
     * Mock the chunk repository to return the chunks at the given positions.
     * @param gameMapRepository the game map repository
     * @param mapId the map id
     * @param chunks the chunks to return
     */
    private static void mockGameMap(GameMapRepository gameMapRepository, String mapId, GameMap gameMap,
                                    Map<Vector2C, Chunk> chunks) {
        when(gameMapRepository.findById(eq(mapId))).thenReturn(Optional.of(gameMap));
        when(gameMapRepository.findByIdAndGetChunks(eq(mapId), any())).thenAnswer(x -> {
            Collection<Vector2C> positions = x.getArgument(1);
            Map<Vector2C, Chunk> result = new HashMap<>();
            positions.forEach(position -> result.put(position, chunks.get(position)));
            gameMap.setChunks(result);
            return Optional.of(gameMap);
        });
    }
}
