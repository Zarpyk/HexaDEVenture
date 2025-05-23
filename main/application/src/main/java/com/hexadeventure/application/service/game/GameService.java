package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameInCombatException;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.SizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialCharacter;
import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.map.*;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.map.resources.ResourceType;
import com.hexadeventure.model.movement.EnemyMovement;
import com.hexadeventure.model.movement.MovementAction;
import com.hexadeventure.model.movement.MovementResponse;
import com.hexadeventure.model.movement.ResourceAction;
import com.hexadeventure.model.user.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides game-related functionalities such as starting a game, managing map chunks,
 * handling player movement, and processing map entities like resources and enemies.
 * This service acts as the implementation of the GameUseCase interface and encapsulates
 * the core game logic.
 */
public class GameService implements GameUseCase {
    // Use only odd numbers
    public static final int MIN_SQUARE_SIZE = 3;
    public static final int MIN_MAP_SIZE = Chunk.SIZE * (MIN_SQUARE_SIZE * MIN_SQUARE_SIZE);
    
    /**
     * The distance in chunks from the player chunk to render
     */
    public static final int RENDER_DISTANCE = 1;
    
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    private final NoiseGenerator noiseGenerator;
    private final AStarPathfinder aStarPathfinder;
    private final SettingsImporter settingsImporter;
    
    public GameService(UserRepository userRepository, GameMapRepository gameMapRepository,
                       NoiseGenerator noiseGenerator, AStarPathfinder aStarPathfinder,
                       SettingsImporter settingsImporter) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.noiseGenerator = noiseGenerator;
        this.aStarPathfinder = aStarPathfinder;
        this.settingsImporter = settingsImporter;
    }
    
    @Override
    public void startGame(String email, long seed, int size) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        assert userOptional.isPresent();
        User user = userOptional.get();
        if(user.getMapId() != null) throw new GameStartedException();
        if(size < MIN_MAP_SIZE) {
            throw new SizeException("Map size must be greater than " + MIN_MAP_SIZE);
        }
        if(size % 16 != 0) {
            throw new SizeException("Map size must be a multiple of 16");
        }
        MapGenerator mapGenerator = new MapGenerator(noiseGenerator, aStarPathfinder, settingsImporter);
        GameMap map = mapGenerator.initialMapGeneration(email, seed, size);
        addInitialResources(map);
        
        user.setMapId(map.getId());
        user.setPlayedGames(user.getPlayedGames() + 1);
        user.setCurrentGameStartTime(LocalDateTime.now());
        
        gameMapRepository.save(map);
        userRepository.save(user);
    }
    
    private void addInitialResources(GameMap map) {
        InitialResources initialResources = settingsImporter.importInitialResources();
        Map<String, WeaponSetting> weapons = settingsImporter.importWeapons();
        Map<String, Food> foods = settingsImporter.importFoods();
        Map<String, Potion> potions = settingsImporter.importPotions();
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        
        SplittableRandom random = new SplittableRandom(map.getSeed());
        
        for (InitialCharacter characterData : initialResources.getInitialCharacters()) {
            int health = random.nextInt(characterData.getMinHealth(), characterData.getMaxHealth() + 1);
            int speed = random.nextInt(characterData.getMinSpeed(), characterData.getMaxSpeed() + 1);
            PlayableCharacter playableCharacter = new PlayableCharacter(characterData.getName(), health, speed);
            map.getInventory().addCharacter(playableCharacter);
        }
        
        for (InitialStringIdResourceData weaponsData : initialResources.getInitialWeapons()) {
            WeaponSetting weaponSetting = weapons.get(weaponsData.getId());
            map.getInventory().addItem(new Weapon(weaponSetting, random), weaponsData.getCount());
        }
        
        for (InitialStringIdResourceData foodsData : initialResources.getInitialFoods()) {
            map.getInventory().addItem(foods.get(foodsData.getId()), foodsData.getCount());
        }
        
        for (InitialStringIdResourceData potionsData : initialResources.getInitialPotions()) {
            map.getInventory().addItem(potions.get(potionsData.getId()), potionsData.getCount());
        }
        
        for (InitialResourceTypeIdResourceData resourcesData : initialResources.getInitialMaterials()) {
            map.getInventory().addItem(materials.get(resourcesData.getId()), resourcesData.getCount());
        }
    }
    
    @Override
    public ChunkData getChunks(String email) {
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
        
        MainCharacter mainCharacter = gameMap.getMainCharacter();
        Vector2C currentChunk = Chunk.getChunkPosition(mainCharacter.getPosition());
        
        // Get chunks around the player
        Set<Vector2C> chunkAroundPlayer = currentChunk.getAroundPositions(RENDER_DISTANCE, false);
        Map<Vector2C, Chunk> mapChunks = gameMapRepository.findMapChunks(gameMap.getId(), chunkAroundPlayer);
        
        return new ChunkData(mapChunks,
                             mainCharacter);
    }
    
    @Override
    public MovementResponse move(String email, Vector2 positionToMove) {
        User user = Utilities.getUser(email, userRepository);
        GameMap gameMap = Utilities.getGameMap(user, gameMapRepository);
        
        // Check if the game is in combat
        if(gameMap.isInCombat()) throw new GameInCombatException();
        
        List<MovementAction> actions = new ArrayList<>();
        
        MainCharacter mainCharacter = gameMap.getMainCharacter();
        Vector2C currentChunkPosition = Chunk.getChunkPosition(mainCharacter.getPosition());
        
        // Get chunks around the player
        Set<Vector2C> chunksAroundPlayer = currentChunkPosition.getAroundPositions(RENDER_DISTANCE,
                                                                                   false);
        Map<Vector2C, Chunk> chunksCache = gameMapRepository.findMapChunks(gameMap.getId(),
                                                                           chunksAroundPlayer);
        gameMap.addChunks(chunksCache, false);
        
        // Check the position to move is not a wall
        CellData cell = gameMap.getCell(positionToMove);
        if(cell.getType() == CellType.WALL) {
            return new MovementResponse(actions, false);
        }
        
        // Generate the player path
        Queue<Vector2> path = aStarPathfinder.generatePath(mainCharacter.getPosition(),
                                                           positionToMove,
                                                           gameMap.getCostMap(chunksAroundPlayer,
                                                                              true));
        // Discard the first position of the path because it is the player's actual position
        path.poll();
        
        Vector2 oldPosition = mainCharacter.getPosition();
        Vector2 position = mainCharacter.getPosition();
        boolean startCombat = false;
        boolean newChunk = false;
        while (!path.isEmpty() && !startCombat) {
            position = path.poll();
            
            // Register the traveled distance
            user.setTravelledDistance(user.getTravelledDistance() + 1);
            
            // Check if chunks around the player are changed
            Set<Vector2C> newChunkPositions = Chunk.getChunkPosition(position)
                                                   .getAroundPositions(RENDER_DISTANCE,
                                                                       false);
            if(!newChunkPositions.equals(chunksAroundPlayer)) {
                // Generate new chunks around the player
                chunksAroundPlayer = newChunkPositions;
                chunksCache = gameMapRepository.findMapChunks(gameMap.getId(), newChunkPositions);
                Map<Vector2C, Chunk> findedChunks = chunksCache;
                Set<Vector2C> nonExistingChunks = newChunkPositions.stream()
                                                                   .filter(chunk -> !findedChunks.containsKey(chunk))
                                                                   .collect(Collectors.toSet());
                MapGenerator mapGenerator = new MapGenerator(noiseGenerator, aStarPathfinder, settingsImporter);
                mapGenerator.generateSelectedChunks(gameMap, nonExistingChunks);
                // If new chunk exist, add it to the map
                gameMap.addChunks(chunksCache, false);
                // Update the cache
                chunksCache = gameMap.getChunks();
                newChunk = true;
            }
            
            // Check resource on the position
            ResourceAction resourceAction = processResourceAction(gameMap, position, user);
            
            // Process the enemies
            List<EnemyMovement> enemyMovements = new ArrayList<>();
            startCombat = processEnemy(gameMap,
                                       chunksCache,
                                       enemyMovements,
                                       position);
            
            MovementAction movementAction = new MovementAction(oldPosition, position, resourceAction,
                                                               enemyMovements, startCombat);
            actions.add(movementAction);
            oldPosition = position;
        }
        mainCharacter.setPosition(position);
        
        gameMapRepository.save(gameMap);
        userRepository.save(user);
        return new MovementResponse(actions, newChunk);
    }
    
    /**
     * Process the resource on the position.
     * @param gameMap the game map
     * @param position the position of the resource
     * @param user the user
     * @return the resource action
     */
    private ResourceAction processResourceAction(GameMap gameMap, Vector2 position, User user) {
        Resource resource = gameMap.getResource(position);
        ResourceAction resourceAction = null;
        if(resource != null) {
            resourceAction = new ResourceAction(resource.getType(), resource.getCount());
            Material material = settingsImporter.importMaterials().get(resource.getType());
            gameMap.getInventory().addItem(material, resource.getCount());
            gameMap.removeResource(position);
            
            // Update collected resources
            user.setCollectedResources(user.getCollectedResources() + resource.getCount());
        }
        return resourceAction;
    }
    
    /**
     * Process the enemies around the player.
     * @param gameMap the game map
     * @param chunksCache the cache of chunks to avoid finding it again on the database
     * @param enemyMovements the list of enemy movements to be updated
     * @param playerPosition the player position that the enemies will move to
     * @return true if combat started, false otherwise
     */
    private boolean processEnemy(GameMap gameMap,
                                 Map<Vector2C, Chunk> chunksCache,
                                 List<EnemyMovement> enemyMovements,
                                 Vector2 playerPosition) {
        boolean startCombat = false;
        Enemy foundEnemy = gameMap.getEnemy(playerPosition);
        if(foundEnemy != null) {
            // If the player goes to the enemy position, start combat
            startCombat(gameMap, foundEnemy);
            if(foundEnemy.getPosition() == gameMap.getBossPosition()) gameMap.setBossBattle(true);
            gameMap.removeEnemy(playerPosition);
            startCombat = true;
        } else {
            // Move enemies around the player to the player position
            
            Vector2 combatPosition = null;
            for (Chunk arroundChunk : chunksCache.values()) {
                List<Enemy> enemies = new ArrayList<>(arroundChunk.getEnemies().values());
                for (Enemy enemy : enemies) {
                    // Don't move the boss
                    if(enemy.getPosition() == gameMap.getBossPosition()) continue;
                    
                    Queue<Vector2> enemyPath = aStarPathfinder.generatePath(enemy.getPosition(),
                                                                            playerPosition,
                                                                            gameMap.getCostMap(
                                                                                    chunksCache.keySet(),
                                                                                    true,
                                                                                    true,
                                                                                    enemy.getPosition()));
                    
                    if(enemyPath.isEmpty()) continue;
                    
                    // Ignore the first position of the path, because it is the enemy position
                    enemyPath.poll();
                    
                    EnemyMovement enemyMovement;
                    if(enemyPath.size() <= Enemy.MOVEMENT_SPEED) {
                        // If the player is on the enemy range, start combat
                        
                        Vector2 targetPosition = enemyPath.poll();
                        while (!enemyPath.isEmpty()) {
                            targetPosition = enemyPath.poll();
                        }
                        
                        enemyMovement = new EnemyMovement(enemy.getPosition(), targetPosition);
                        gameMap.moveEnemy(enemy.getPosition(), targetPosition);
                        startCombat(gameMap, enemy);
                        startCombat = true;
                        
                        // Save targetPosition to remove outside the loop
                        combatPosition = targetPosition;
                    } else {
                        Vector2 enemyPosition = null;
                        for (int i = 0; i < Enemy.MOVEMENT_SPEED; i++) {
                            if(enemyPath.isEmpty()) break;
                            enemyPosition = enemyPath.poll();
                        }
                        if(enemyPosition == null) throw new IllegalStateException("Enemy targetPosition is null");
                        enemyMovement = new EnemyMovement(enemy.getPosition(), enemyPosition);
                        gameMap.moveEnemy(enemy.getPosition(), enemyPosition);
                    }
                    enemyMovements.add(enemyMovement);
                    if(startCombat) break;
                }
                if(startCombat) break;
            }
            if(combatPosition != null) gameMap.removeEnemy(combatPosition);
        }
        return startCombat;
    }
    
    private static void startCombat(GameMap gameMap, Enemy enemy) {
        gameMap.getCombatTerrain().placeEnemies(enemy.getEnemies());
        gameMap.getCombatTerrain().setLoot(enemy.getLoot(), enemy.getLootSeed());
        gameMap.setInCombat(true);
    }
    
    @Override
    public void finishGame(String email) {
        User user = Utilities.getUser(email, userRepository);
        GameMap gameMap = Utilities.getGameMap(user, gameMapRepository);
        
        user.setMapId(null);
        int playedTime = LocalDateTime.now().getSecond() - user.getCurrentGameStartTime().getSecond();
        user.setPlayedTime(user.getPlayedTime() + playedTime);
        
        gameMapRepository.deleteById(gameMap.getId());
        userRepository.save(user);
    }
}
