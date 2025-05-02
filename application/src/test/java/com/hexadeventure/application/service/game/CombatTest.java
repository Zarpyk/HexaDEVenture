package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.MapFactory;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

public class CombatTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final int TEST_COMBAT_ROW = 0;
    private static final int TEST_COMBAT_COLUMN = 0;
    private static final String TEST_CHARACTER_ID = "testCharacterId";
    private static final String TEST_CHARACTER_NAME = "testCharacterName";
    private static final int TEST_CHARACTER_HEALTH = 100;
    private static final int TEST_CHARACTER_SPEED = 10;
    
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private static final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private static final SettingsImporter settingsImporter = mock(SettingsImporter.class);
    private final CombatService combatService = new CombatService(userRepository, gameMapRepository);
    
    //region GetCombatStatus
    @Test
    public void givenNoStartGameUser_whenGetCombatStatus_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        // Check
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> combatService.getCombatStatus(TEST_USER_EMAIL));
    }
    
    @Test
    public void givenUserNotInCombat_whenGetCombatStatus_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        // Check
        assertThatExceptionOfType(CombatNotStartedException.class)
                .isThrownBy(() -> combatService.getCombatStatus(TEST_USER_EMAIL));
    }
    
    @Test
    public void givenUserInCombat_whenGetCombatStatus_thenReturnCombatStatus() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository,
                                                    aStarPathfinder,
                                                    settingsImporter);
        map.setInCombat(true);
        
        // Execute the method
        CombatTerrain combat = combatService.getCombatStatus(TEST_USER_EMAIL);
        
        // Check
        assertThat(combat).isNotNull();
        assertThat(combat.getPlayerTerrain().length).isEqualTo(MapFactory.COMBAT_ROW_SIZE);
        assertThat(combat.getEnemyTerrain().length).isEqualTo(MapFactory.COMBAT_ROW_SIZE);
        assertThat(combat.getPlayerTerrain()[0].length).isEqualTo(MapFactory.COMBAT_COLUMN_SIZE);
        assertThat(combat.getEnemyTerrain()[0].length).isEqualTo(MapFactory.COMBAT_COLUMN_SIZE);
    }
    //endregion
    
    //region PlaceCharacter
    @Test
    public void givenNoStartGameUser_whenPlaceCharacter_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        // Check
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               TEST_COMBAT_ROW,
                                                               TEST_COMBAT_COLUMN,
                                                               TEST_CHARACTER_ID));
    }
    
    @Test
    public void givenUserNotInCombat_whenPlaceCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        // Check
        assertThatExceptionOfType(CombatNotStartedException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               TEST_COMBAT_ROW,
                                                               TEST_COMBAT_COLUMN,
                                                               TEST_CHARACTER_ID));
    }
    
    @Test
    public void givenInvalidPosition_whenPlaceCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Check
        assertThatExceptionOfType(InvalidPositionException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               -1,
                                                               -1,
                                                               TEST_CHARACTER_ID));
        assertThatExceptionOfType(InvalidPositionException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               MapFactory.COMBAT_ROW_SIZE,
                                                               MapFactory.COMBAT_COLUMN_SIZE,
                                                               TEST_CHARACTER_ID));
    }
    
    @Test
    public void givenCharacterNotOnInventory_whenPlaceCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Check
        assertThatExceptionOfType(CharacterNotFoundException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               TEST_COMBAT_ROW,
                                                               TEST_COMBAT_COLUMN,
                                                               TEST_CHARACTER_ID));
    }
    
    @Test
    public void givenPositionAndCharacter_whenPlaceCharacter_thenPlaceCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository,
                                                    aStarPathfinder,
                                                    settingsImporter);
        map.setInCombat(true);
        
        // Add character to inventory
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        map.getInventory().addCharacter(playableCharacter);
        
        // Execute the method
        combatService.placeCharacter(TEST_USER_EMAIL, TEST_COMBAT_ROW, TEST_COMBAT_COLUMN, playableCharacter.getId());
        
        // Check
        CombatTerrain combat = combatService.getCombatStatus(TEST_USER_EMAIL);
        assertThat(combat.getPlayerTerrain()[TEST_COMBAT_ROW][TEST_COMBAT_COLUMN]).isNotNull();
        assertThat(combat.getPlayerTerrain()[TEST_COMBAT_ROW][TEST_COMBAT_COLUMN]).isEqualTo(playableCharacter);
        assertThat(map.getInventory().getCharacters()).doesNotContainKey(playableCharacter.getId());
    }
    
    @Test
    public void givenOccupiedPosition_whenPlaceCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository,
                                                    aStarPathfinder,
                                                    settingsImporter);
        map.setInCombat(true);
        
        // Add character to inventory
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        PlayableCharacter playableCharacter2 = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                     TEST_CHARACTER_HEALTH,
                                                                     TEST_CHARACTER_SPEED);
        map.getInventory().addCharacter(playableCharacter);
        map.getInventory().addCharacter(playableCharacter2);
        
        // Execute the method
        combatService.placeCharacter(TEST_USER_EMAIL, TEST_COMBAT_ROW, TEST_COMBAT_COLUMN, playableCharacter.getId());
        
        // Check
        assertThatExceptionOfType(PositionOccupiedException.class)
                .isThrownBy(() -> combatService.placeCharacter(TEST_USER_EMAIL,
                                                               TEST_COMBAT_ROW,
                                                               TEST_COMBAT_COLUMN,
                                                               playableCharacter2.getId()));
    }
    //endregion
    
    //region RemoveCharacter
    @Test
    public void givenNoStartGameUser_whenRemoveCharacter_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        // Check
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> combatService.removeCharacter(TEST_USER_EMAIL,
                                                                TEST_COMBAT_ROW,
                                                                TEST_COMBAT_COLUMN));
    }
    
    @Test
    public void givenUserNotInCombat_whenRemoveCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        // Check
        assertThatExceptionOfType(CombatNotStartedException.class)
                .isThrownBy(() -> combatService.removeCharacter(TEST_USER_EMAIL,
                                                                TEST_COMBAT_ROW,
                                                                TEST_COMBAT_COLUMN));
    }
    
    @Test
    public void givenInvalidPosition_whenRemoveCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Check
        assertThatExceptionOfType(InvalidPositionException.class)
                .isThrownBy(() -> combatService.removeCharacter(TEST_USER_EMAIL,
                                                                -1,
                                                                -1));
        assertThatExceptionOfType(InvalidPositionException.class)
                .isThrownBy(() -> combatService.removeCharacter(TEST_USER_EMAIL,
                                                                MapFactory.COMBAT_ROW_SIZE,
                                                                MapFactory.COMBAT_COLUMN_SIZE));
    }
    
    @Test
    public void givenEmptyPosition_whenRemoveCharacter_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Check
        assertThatExceptionOfType(PositionEmptyException.class)
                .isThrownBy(() -> combatService.removeCharacter(TEST_USER_EMAIL,
                                                                TEST_COMBAT_ROW,
                                                                TEST_COMBAT_COLUMN));
    }
    
    @Test
    public void givenValidPosition_whenRemoveCharacter_thenRemoveCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository,
                                                    aStarPathfinder,
                                                    settingsImporter);
        map.setInCombat(true);
        
        // Add character to inventory
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        map.getInventory().addCharacter(playableCharacter);
        // Place character on the combat terrain
        combatService.placeCharacter(TEST_USER_EMAIL, TEST_COMBAT_ROW, TEST_COMBAT_COLUMN, playableCharacter.getId());
        
        // Execute the method
        combatService.removeCharacter(TEST_USER_EMAIL, TEST_COMBAT_ROW, TEST_COMBAT_COLUMN);
        
        // Check
        CombatTerrain combat = combatService.getCombatStatus(TEST_USER_EMAIL);
        assertThat(combat.getPlayerTerrain()[TEST_COMBAT_ROW][TEST_COMBAT_COLUMN]).isNull();
        assertThat(map.getInventory().getCharacters()).containsKey(playableCharacter.getId());
        assertThat(map.getInventory().getCharacters()).containsValue(playableCharacter);
    }
    //endregion
}
