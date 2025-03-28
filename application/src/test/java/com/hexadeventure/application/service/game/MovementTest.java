package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.MapFactory;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.movement.MovementAction;
import com.hexadeventure.model.movement.MovementResponse;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MovementTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private static final ChunkRepository chunkRepository = mock(ChunkRepository.class);
    private static final NoiseGenerator noiseGenerator = mock(NoiseGenerator.class);
    private static final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private static final GameService gameService = new GameService(userRepository, gameMapRepository,
                                                                   noiseGenerator, aStarPathfinder);
    
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
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        
        MapFactory.createEmptyGameMap(gameMapRepository, chunkRepository, aStarPathfinder);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.EMPTY_END_POSITION);
        
        assertThat(move.actions()).hasSize(MapFactory.EMPTY_MAP_PATH_LENGTH);
        assertThat(move.actions().getFirst().x()).isEqualTo(MapFactory.EMPTY_START_POSITION.x);
        assertThat(move.actions().getFirst().y()).isEqualTo(MapFactory.EMPTY_START_POSITION.y);
        assertThat(move.actions().getLast().x()).isEqualTo(MapFactory.EMPTY_END_POSITION.x);
        assertThat(move.actions().getLast().y()).isEqualTo(MapFactory.EMPTY_END_POSITION.y);
    }
    
    @Test
    public void givenPositionWithObstacle_whenMove_thenCancelMovement() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.OBSTACLE_MAP_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        
        MapFactory.createObstacleGameMap(gameMapRepository, chunkRepository, aStarPathfinder);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.OBSTACLE_END_POSITION);
        
        assertThat(move.actions()).hasSize(0);
    }
    
    @Test
    public void givenPositionWithPathWithResource_whenMove_thenCollectResource() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.RESOURCE_MAP_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        
        GameMap gameMap = MapFactory.createResourceGameMap(gameMapRepository, chunkRepository, aStarPathfinder);
        
        MovementResponse move = gameService.move(TEST_USER_EMAIL, MapFactory.RESOURCE_END_POSITION);
        
        List<MovementAction> actions = move.actions();
        
        assertThat(actions).hasSize(MapFactory.RESOURCE_MAP_PATH_LENGTH);
        MovementAction first = actions.getFirst();
        assertThat(first.x()).isEqualTo(MapFactory.RESOURCE_START_POSITION.x);
        assertThat(first.y()).isEqualTo(MapFactory.RESOURCE_START_POSITION.y);
        assertThat(first.resourceAction()).isNull();
        assertThat(gameMap.getResource(new Vector2(first.x(), first.y()))).isNull();
        
        for (int i = 1; i < actions.size() - 1; i++) {
            MovementAction action = actions.get(i);
            assertThat(action.resourceAction()).isNotNull();
            assertThat(gameMap.getResource(new Vector2(action.x(), action.y()))).isNull();
        }
        
        MovementAction last = actions.getLast();
        assertThat(last.x()).isEqualTo(MapFactory.RESOURCE_END_POSITION.x);
        assertThat(last.y()).isEqualTo(MapFactory.RESOURCE_END_POSITION.y);
        assertThat(last.resourceAction()).isNotNull();
        assertThat(gameMap.getResource(new Vector2(last.x(), last.y()))).isNull();
        
        Map<String, Item> items = gameMap.getInventory().getItems();
        assertThat(items.size()).isEqualTo(1);
        Optional<Item> item = items.values().stream().findFirst();
        assertThat(item).isNotEmpty();
        assertThat(item.get().getType()).isEqualTo(ItemType.MATERIAL);
        
        GameMap checkMap = MapFactory.createResourceGameMap(gameMapRepository, chunkRepository, aStarPathfinder);
        int resourceCount = 0;
        for (int i = 1; i < actions.size(); i++) {
            MovementAction action = actions.get(i);
            Resource resource = checkMap.getResource(new Vector2(action.x(), action.y()));
            resourceCount += resource.getCount();
        }
        
        assertThat(item.get().getCount()).isEqualTo(resourceCount);
    }
}
