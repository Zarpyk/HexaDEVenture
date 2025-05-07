package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.ItemFactory;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StartGameTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = GameService.MIN_MAP_SIZE;
    private static final Set<Vector2C> chunksToGenerate = new HashSet<>();
    
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private final NoiseGenerator noiseGenerator = mock(NoiseGenerator.class);
    private final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private final SettingsImporter settingsImporter = mock(SettingsImporter.class);
    private final GameService gameService = new GameService(userRepository,
                                                            gameMapRepository,
                                                            noiseGenerator,
                                                            aStarPathfinder,
                                                            settingsImporter);
    
    static {
        int center = TEST_SIZE / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(center, center);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                chunksToGenerate.add(centerChunk.add(i, j));
            }
        }
    }
    
    @BeforeEach
    public void beforeEach() {
        when(noiseGenerator.getCircleWithNoisyEdge(anyInt(),
                                                   any(),
                                                   anyLong(),
                                                   anyInt(),
                                                   eq(chunksToGenerate))).thenReturn(new HashMap<>());
        
        when(aStarPathfinder.generatePath(any(), any(), any())).thenReturn(new LinkedList<>());
        
        ItemFactory.setupSettingsImporter(settingsImporter);
    }
    
    @Test
    public void givenEmailSeedAndSize_whenItDontHaveStartedGame_thenCreateNewMap() {
        UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        
        verify(noiseGenerator, atLeast(1)).initNoise(any(),
                                                     eq(TEST_SEED),
                                                     anyDouble(),
                                                     anyInt(),
                                                     anyDouble(),
                                                     anyDouble(),
                                                     anyInt(),
                                                     anyBoolean(),
                                                     anyBoolean());
        
        verify(noiseGenerator, atLeast(1)).getPerlinNoise(anyDouble(), anyDouble(), any(), anyBoolean());
        verify(noiseGenerator, atLeast(1)).releaseNoise(any());
        
        verify(userRepository, times(1)).updateMapIdByEmail(eq(UserFactory.EMAIL), any());
        verify(gameMapRepository, times(1)).save(any());
    }
    
    @Test
    public void givenEmailSeedAndSize_whenItHaveStartedGame_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        verify(gameMapRepository, times(1)).save(any());
        
        Optional<GameMap> gameMap = Optional.of(new GameMap(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE));
        when(gameMapRepository.findById(any())).thenReturn(gameMap);
        testUser.setMapId(gameMap.get().getId());
        when(userRepository.findByEmail(eq(TEST_USER_EMAIL))).thenReturn(Optional.of(testUser));
        
        assertThatExceptionOfType(GameStartedException.class).isThrownBy(() -> gameService.startGame(TEST_USER_EMAIL,
                                                                                                     TEST_SEED,
                                                                                                     TEST_SIZE));
        
        verify(gameMapRepository, times(1)).save(any());
    }
    
    @Test
    public void givenEmailSeedAndSize_whenCreateNewMap_thenPlayerIsAdded() {
        UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        
        ArgumentCaptor<GameMap> captor = ArgumentCaptor.forClass(GameMap.class);
        verify(gameMapRepository).save(captor.capture());
        
        GameMap gameMap = captor.getValue();
        assertThat(gameMap.getMainCharacter().getPosition()).isEqualTo(new Vector2(TEST_SIZE / 2, TEST_SIZE / 2));
    }
    
    @ParameterizedTest(name = "Given size {0} when create new map then throw exception")
    @ValueSource(ints = {0, GameService.MIN_MAP_SIZE - 1})
    public void givenSmallSize_whenCreateNewMap_thenThrowException(int size) {
        UserFactory.createTestUser(userRepository);
        
        assertThatExceptionOfType(MapSizeException.class).isThrownBy(() -> gameService.startGame(TEST_USER_EMAIL,
                                                                                                 TEST_SEED,
                                                                                                 size));
    }
    
    @ParameterizedTest(name = "Given size {0} when create new map then throw exception")
    @ValueSource(ints = {GameService.MIN_MAP_SIZE + 1, GameService.MIN_MAP_SIZE + Chunk.SIZE - 1})
    public void givenNotMultipleOfChunkSize_whenCreateNewMap_thenThrowException(int size) {
        UserFactory.createTestUser(userRepository);
        
        assertThatExceptionOfType(MapSizeException.class).isThrownBy(() -> gameService.startGame(TEST_USER_EMAIL,
                                                                                                 TEST_SEED,
                                                                                                 size));
    }
    
    @Test
    public void givenInitialResources_whenCreateNewMap_thenAddItToInventory() {
        UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        ArgumentCaptor<GameMap> captor = ArgumentCaptor.forClass(GameMap.class);
        verify(gameMapRepository).save(captor.capture());
        
        GameMap gameMap = captor.getValue();
        assertThat(gameMap.getInventory().getCharacters()).isNotEmpty();
        for (Item item : gameMap.getInventory().getItems().values()) {
            if(item instanceof Weapon weapon) {
                assertThat(weapon.getName()).isEqualTo(ItemFactory.TEST_WEAPON_NAME);
            } else if(item instanceof Food food) {
                assertThat(food.getName()).isEqualTo(ItemFactory.TEST_FOOD_NAME);
            } else if(item instanceof Potion potion) {
                assertThat(potion.getName()).isEqualTo(ItemFactory.TEST_POTION_NAME);
            } else if(item instanceof Material material) {
                assertThat(material.getMaterialType()).isEqualTo(ItemFactory.TEST_MATERIAL_TYPE);
            }
        }
    }
}
