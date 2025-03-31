package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameInCombatException;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponData;
import com.hexadeventure.model.map.*;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.map.resources.ResourceType;
import com.hexadeventure.model.movement.EnemyMovement;
import com.hexadeventure.model.movement.MovementAction;
import com.hexadeventure.model.movement.MovementResponse;
import com.hexadeventure.model.movement.ResourceAction;
import com.hexadeventure.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Transactional
    public void startGame(String email, long seed, int size) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() != null) throw new GameStartedException();
        if(size < MIN_MAP_SIZE) {
            throw new MapSizeException("Map size must be greater than " + MIN_MAP_SIZE);
        }
        if(size % 16 != 0) {
            throw new MapSizeException("Map size must be a multiple of 16");
        }
        MapGenerator mapGenerator = new MapGenerator(noiseGenerator, aStarPathfinder);
        GameMap map = mapGenerator.initialMapGeneration(email, seed, size);
        addInitialResources(map);
        
        gameMapRepository.save(map);
        userRepository.updateMapIdByEmail(email, map.getId());
    }
    
    private void addInitialResources(GameMap map) {
        InitialResources initialResources = settingsImporter.importInitialResources();
        Map<String, WeaponData> weapons = settingsImporter.importWeapons();
        Map<String, Food> foods = settingsImporter.importFoods();
        Map<String, Potion> potions = settingsImporter.importPotions();
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        
        SplittableRandom random = new SplittableRandom(map.getSeed());
        
        for (InitialStringIdResourceData weaponsData : initialResources.getInitialWeapons()) {
            WeaponData weaponData = weapons.get(weaponsData.getId());
            map.getInventory().addItem(new Weapon(weaponData, random), weaponsData.getCount());
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
    public MovementResponse move(String email, Vector2 positionToMove) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        
        // Check if the game has started
        if(user.get().getMapId() == null) throw new GameNotStartedException();
        
        Optional<GameMap> map = gameMapRepository.findById(user.get().getMapId());
        assert map.isPresent();
        GameMap gameMap = map.get();
        
        // Check if the game is in combat
        if(gameMap.isInCombat()) throw new GameInCombatException();
        
        List<MovementAction> actions = new ArrayList<>();
        
        MainCharacter mainCharacter = gameMap.getMainCharacter();
        Vector2C currentChunk = Chunk.getChunkPosition(mainCharacter.getPosition());
        
        // Get chunks around the player
        Set<Vector2C> chunkArroundPlayer = currentChunk.getArroundPositions(RENDER_DISTANCE, false);
        Map<Vector2C, Chunk> mapChunks = gameMapRepository.findMapChunks(gameMap.getId(), chunkArroundPlayer);
        gameMap.addChunks(mapChunks, false);
        
        // Check the position to move is not a wall
        CellData cell = gameMap.getCell(positionToMove);
        if(cell.getType() == CellType.WALL) {
            return new MovementResponse(actions);
        }
        
        // Generate the path
        Queue<Vector2> path = aStarPathfinder.generatePath(mainCharacter.getPosition(),
                                                           positionToMove,
                                                           gameMap.getCostMap(chunkArroundPlayer, true));
        Vector2 position = mainCharacter.getPosition();
        boolean startCombat = false;
        while (!path.isEmpty() && !startCombat) {
            position = path.poll();
            
            // Check resource on the position
            Chunk chunk = gameMap.getChunkOfCell(position);
            Resource resource = chunk.getResource(position);
            ResourceAction resourceAction = null;
            if(resource != null) {
                resourceAction = new ResourceAction(resource.getType().ordinal(), resource.getCount());
                Material material = settingsImporter.importMaterials().get(resource.getType());
                gameMap.getInventory().addItem(material, resource.getCount());
                chunk.removeResource(position);
            }
            
            // Update enemies around the player
            List<EnemyMovement> enemyMovements = new ArrayList<>();
            Set<Vector2C> newChunkPositions = currentChunk.getArroundPositions(RENDER_DISTANCE, false);
            if(!newChunkPositions.equals(chunkArroundPlayer)) {
                chunkArroundPlayer = newChunkPositions;
                mapChunks = gameMapRepository.findMapChunks(gameMap.getId(), chunkArroundPlayer);
                gameMap.addChunks(mapChunks, false);
            }
            for (Chunk arroundChunk : mapChunks.values()) {
                for (Enemy enemy : arroundChunk.getEnemies().values()) {
                    Queue<Vector2> enemyPath = aStarPathfinder.generatePath(enemy.getPosition(),
                                                                            position,
                                                                            gameMap.getCostMap(chunkArroundPlayer,
                                                                                               true));
                    // Ignore first position
                    enemyPath.poll();
                    
                    EnemyMovement enemyMovement;
                    if(enemyPath.size() <= Enemy.MOVEMENT_SPEED) {
                        // If the player in on the enemy range, start combat
                        gameMap.moveEnemy(enemy.getPosition(), position);
                        enemyMovement = new EnemyMovement(position.x, position.y);
                        startCombat = true;
                        gameMap.setInCombat(true);
                    } else {
                        Vector2 enemyPosition = null;
                        for (int i = 0; i < Enemy.MOVEMENT_SPEED; i++) {
                            if(enemyPath.isEmpty()) break;
                            enemyPosition = enemyPath.poll();
                        }
                        if(enemyPosition == null) throw new IllegalStateException("Enemy position is null");
                        gameMap.moveEnemy(enemy.getPosition(), enemyPosition);
                        enemyMovement = new EnemyMovement(enemyPosition.x, enemyPosition.y);
                    }
                    enemyMovements.add(enemyMovement);
                    if(startCombat) break;
                }
                if(startCombat) break;
            }
            
            MovementAction movementAction = new MovementAction(position.x, position.y, resourceAction, enemyMovements);
            actions.add(movementAction);
        }
        mainCharacter.setPosition(position);
        
        gameMapRepository.save(gameMap);
        return new MovementResponse(actions);
    }
}
