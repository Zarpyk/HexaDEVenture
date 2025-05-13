package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameInCombatException;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.MapFactory;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.map.ChunkData;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.movement.EnemyMovement;
import com.hexadeventure.model.movement.MovementAction;
import com.hexadeventure.model.movement.MovementResponse;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;


public class MovementTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private static final NoiseGenerator noiseGenerator = mock(NoiseGenerator.class);
    private static final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private static final SettingsImporter settingsImporter = mock(SettingsImporter.class);
    private static final GameService gameService = new GameService(userRepository, gameMapRepository,
                                                                   noiseGenerator, aStarPathfinder,
                                                                   settingsImporter);
    
    @BeforeEach
    public void beforeEach() {
        // Reset the mocks before each test
        reset(userRepository, gameMapRepository);
    }
    
    //region GetChunks
    @Test
    public void givenNoStartGameUser_whenGetChunks_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> gameService.getChunks(TEST_USER_EMAIL));
    }
    
    @Test
    public void givenStartGame_whenGetChunks_thenGetChunksArroundPlayer() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        ChunkData chunkData = gameService.getChunks(TEST_USER_EMAIL);
        
        assertThat(chunkData.chunks().size()).isEqualTo(9);
        assertThat(chunkData.mainCharacter().getPosition()).isEqualTo(MapFactory.EMPTY_START_POSITION);
    }
    //endregion
    
    //region Move
    @Test
    public void givenNoStartGameUser_whenMove_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> gameService.move(TEST_USER_EMAIL, MapFactory.EMPTY_END_POSITION));
    }
    
    @Test
    public void givenPosition_whenMove_thenMoveToThePosition() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap gameMap = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.EMPTY_END_POSITION);
        
        assertThat(move.actions()).hasSize(MapFactory.EMPTY_MAP_PATH_LENGTH);
        Vector2 first = move.actions().getFirst().position();
        assertThat(first.x).isEqualTo(MapFactory.EMPTY_START_POSITION.x);
        assertThat(first.y).isEqualTo(MapFactory.EMPTY_START_POSITION.y);
        Vector2 last = move.actions().getLast().position();
        assertThat(last.x).isEqualTo(MapFactory.EMPTY_END_POSITION.x);
        assertThat(last.y).isEqualTo(MapFactory.EMPTY_END_POSITION.y);
        
        assertThat(gameMap.getMainCharacter().getPosition()).isEqualTo(MapFactory.EMPTY_END_POSITION);
        
        verify(gameMapRepository, times(1)).save(gameMap);
    }
    
    @Test
    public void givenPositionWithObstacle_whenMove_thenCancelMovement() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.OBSTACLE_MAP_ID);
        
        MapFactory.createObstacleGameMap(gameMapRepository, settingsImporter);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.OBSTACLE_END_POSITION);
        
        assertThat(move.actions()).hasSize(0);
    }
    
    @Test
    public void givenPositionWithPathWithResource_whenMove_thenCollectResource() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.RESOURCE_MAP_ID);
        
        GameMap gameMap = MapFactory.createResourceGameMap(gameMapRepository,
                                                           aStarPathfinder,
                                                           settingsImporter,
                                                           false);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.RESOURCE_END_POSITION);
        
        List<MovementAction> actions = move.actions();
        
        assertThat(actions).hasSize(MapFactory.RESOURCE_MAP_PATH_LENGTH);
        MovementAction first = actions.getFirst();
        assertThat(first.position().x).isEqualTo(MapFactory.RESOURCE_START_POSITION.x);
        assertThat(first.position().y).isEqualTo(MapFactory.RESOURCE_START_POSITION.y);
        assertThat(first.resourceAction()).isNull();
        assertThat(gameMap.getResource(new Vector2(first.position().x, first.position().y))).isNull();
        
        for (int i = 1; i < actions.size() - 1; i++) {
            MovementAction action = actions.get(i);
            assertThat(action.resourceAction()).isNotNull();
            assertThat(gameMap.getResource(new Vector2(action.position().x, action.position().y))).isNull();
        }
        
        MovementAction last = actions.getLast();
        assertThat(last.position().x).isEqualTo(MapFactory.RESOURCE_END_POSITION.x);
        assertThat(last.position().y).isEqualTo(MapFactory.RESOURCE_END_POSITION.y);
        assertThat(last.resourceAction()).isNotNull();
        assertThat(gameMap.getResource(new Vector2(last.position().x, last.position().y))).isNull();
        
        Map<String, Item> items = gameMap.getInventory().getItems();
        assertThat(items.size()).isEqualTo(1);
        Optional<Item> item = items.values().stream().findFirst();
        assertThat(item).isNotEmpty();
        assertThat(item.get().getType()).isEqualTo(ItemType.MATERIAL);
        
        GameMap checkMap = MapFactory.createResourceGameMap(gameMapRepository,
                                                            aStarPathfinder,
                                                            settingsImporter,
                                                            true);
        int resourceCount = 0;
        for (int i = 1; i < actions.size(); i++) {
            MovementAction action = actions.get(i);
            Resource resource = checkMap.getResource(new Vector2(action.position().x, action.position().y));
            resourceCount += resource.getCount();
        }
        
        assertThat(item.get().getCount()).isEqualTo(resourceCount);
    }
    
    @Test
    public void givenPositionWithPathWithEnemy_whenMove_thenTheEnemyMoveToPlayer() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.ENEMY_MAP_ID);
        
        GameMap gameMap = MapFactory.createEnemyGameMap(gameMapRepository,
                                                        aStarPathfinder,
                                                        settingsImporter);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.ENEMY_END_POSITION);
        
        List<MovementAction> actions = move.actions();
        
        assertThat(actions).hasSize(MapFactory.ENEMY_MAP_ENEMY_OFFSET - 1);
        MovementAction first = actions.getFirst();
        assertThat(first.position().x).isEqualTo(MapFactory.ENEMY_START_POSITION.x);
        assertThat(first.position().y).isEqualTo(MapFactory.ENEMY_START_POSITION.y);
        assertThat(first.enemyMovements()).hasSize(1);
        
        for (int i = 1; i < actions.size() - 1; i++) {
            MovementAction action = actions.get(i);
            assertThat(action.enemyMovements()).hasSize(1);
        }
        
        MovementAction last = actions.getLast();
        EnemyMovement enemyMovement = last.enemyMovements().stream().findFirst().orElse(null);
        assertThat(enemyMovement).isNotNull();
        assertThat(last.position().x).isEqualTo(enemyMovement.position().x);
        assertThat(last.position().y).isEqualTo(enemyMovement.position().y);
        
        assertThat(gameMap.isInCombat()).isTrue();
    }
    
    @Test
    public void givenPositionWithPathWithEnemy_whenMoveToSamePositionAsPlayer_thenEnemyIsRemovedFromMap() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.ENEMY_MAP_ID);
        
        GameMap gameMap = MapFactory.createEnemyGameMap(gameMapRepository,
                                                        aStarPathfinder,
                                                        settingsImporter);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.ENEMY_END_POSITION);
        
        List<MovementAction> actions = move.actions();
        
        MovementAction last = actions.getLast();
        EnemyMovement enemyMovement = last.enemyMovements().stream().findFirst().orElse(null);
        assertThat(enemyMovement).isNotNull();
        assertThat(last.position().x).isEqualTo(enemyMovement.position().x);
        assertThat(last.position().y).isEqualTo(enemyMovement.position().y);
        
        Enemy enemy = gameMap.getEnemy(enemyMovement.position());
        assertThat(enemy).isNull();
    }
    
    @Test
    public void givenPositionWithPathWithEnemy_whenMove_thenTheLootIsSavedOnTheMap() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.ENEMY_MAP_ID);
        
        GameMap gameMap = MapFactory.createEnemyGameMap(gameMapRepository,
                                                        aStarPathfinder,
                                                        settingsImporter);
        
        gameService.move(TEST_USER_EMAIL, MapFactory.ENEMY_END_POSITION);
        
        assertThat(gameMap.isInCombat()).isTrue();
        assertThat(gameMap.getCombatTerrain().getLoot()).isNotEmpty();
        assertThat(gameMap.getCombatTerrain().getLootSeed()).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void givenMapInCombat_whenMove_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.ENEMY_MAP_ID);
        
        GameMap gameMap = MapFactory.createEnemyGameMap(gameMapRepository,
                                                        aStarPathfinder,
                                                        settingsImporter);
        gameMap.setInCombat(true);
        
        assertThatExceptionOfType(GameInCombatException.class)
                .isThrownBy(() -> gameService.move(TEST_USER_EMAIL, MapFactory.ENEMY_END_POSITION));
    }
    
    @Test
    public void givenPosition_whenMove_thenUpdateTravelPosition() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        gameService.move(TEST_USER_EMAIL, MapFactory.EMPTY_END_POSITION);
        
        assertThat(testUser.getTravelledDistance()).isEqualTo(MapFactory.EMPTY_MAP_PATH_LENGTH);
        
        verify(userRepository, times(1)).save(any());
    }
    
    @Test
    public void givenResources_whenMove_thenUpdateCollectedResources() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.RESOURCE_MAP_ID);
        
        MapFactory.createResourceGameMap(gameMapRepository, aStarPathfinder, settingsImporter, false);
        
        gameService.move(TEST_USER_EMAIL, MapFactory.RESOURCE_END_POSITION);
        
        assertThat(testUser.getCollectedResources()).isEqualTo(MapFactory.RESOURCE_MAP_PATH_LENGTH - 1);
        
        verify(userRepository, times(1)).save(any());
    }
    //endregion
}
