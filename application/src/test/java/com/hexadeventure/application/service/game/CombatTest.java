package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.*;
import com.hexadeventure.model.combat.CombatAction;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.combat.TurnInfo;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.characters.CharacterCombatInfo;
import com.hexadeventure.model.inventory.characters.CharacterStat;
import com.hexadeventure.model.inventory.characters.CharacterStatusChange;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
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
    private final CombatService combatService = new CombatService(userRepository, gameMapRepository, settingsImporter);
    
    
    @BeforeEach
    public void beforeEach() {
        // Reset verify mocks
        reset(gameMapRepository);
    }
    
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
        verify(gameMapRepository, times(1)).save(map);
        
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
        verify(gameMapRepository, times(1)).save(map);
        
        // Execute the method
        combatService.removeCharacter(TEST_USER_EMAIL, TEST_COMBAT_ROW, TEST_COMBAT_COLUMN);
        verify(gameMapRepository, times(2)).save(map);
        
        // Check
        CombatTerrain combat = combatService.getCombatStatus(TEST_USER_EMAIL);
        assertThat(combat.getPlayerTerrain()[TEST_COMBAT_ROW][TEST_COMBAT_COLUMN]).isNull();
        assertThat(map.getInventory().getCharacters()).containsKey(playableCharacter.getId());
        assertThat(map.getInventory().getCharacters()).containsValue(playableCharacter);
    }
    //endregion
    
    //region CharacterCombatInfo
    @Test
    public void givenPlayableCharacter_whenCreateCombatInfo_thenCreateCombatInfo() {
        // Create a playable character
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        playableCharacter.setWeapon(WeaponFactory.createWeapon());
        
        // Create combat info
        CharacterCombatInfo combatInfo = new CharacterCombatInfo(playableCharacter,
                                                                 TEST_COMBAT_ROW,
                                                                 TEST_COMBAT_COLUMN,
                                                                 false);
        
        // Check
        assertThat(combatInfo.getCharacter()).isEqualTo(playableCharacter);
        assertThat(combatInfo.getRow()).isEqualTo(TEST_COMBAT_ROW);
        assertThat(combatInfo.getColumn()).isEqualTo(TEST_COMBAT_COLUMN);
        assertThat(combatInfo.getId()).isEqualTo(playableCharacter.getId());
        assertThat(combatInfo.getName()).isEqualTo(TEST_CHARACTER_NAME);
    }
    
    @Test
    public void givenPlayableCharacterOnFirstRow_whenCreateCombatInfo_thenCreateCombatInfo() {
        // Create a playable character
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        playableCharacter.setWeapon(WeaponFactory.createWeapon());
        
        // Create combat info
        CharacterCombatInfo combatInfo = new CharacterCombatInfo(playableCharacter,
                                                                 0,
                                                                 TEST_COMBAT_COLUMN,
                                                                 false);
        
        // Check
        assertThat(combatInfo.getCurrentAggro()).isEqualTo(
                playableCharacter.getWeapon().getInitialAggro() * CharacterCombatInfo.FIRST_ROW_AGGRO);
    }
    
    @Test
    public void givenPlayableCharacterOnSecondRow_whenCreateCombatInfo_thenCreateCombatInfo() {
        // Create a playable character
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        playableCharacter.setWeapon(WeaponFactory.createWeapon());
        
        // Create combat info
        CharacterCombatInfo combatInfo = new CharacterCombatInfo(playableCharacter,
                                                                 1,
                                                                 TEST_COMBAT_COLUMN,
                                                                 false);
        
        // Check
        assertThat(combatInfo.getCurrentAggro()).isEqualTo(
                playableCharacter.getWeapon().getInitialAggro() * CharacterCombatInfo.SECOND_ROW_AGGRO);
        assertThat(combatInfo.getSpeed()).isEqualTo(
                playableCharacter.getSpeed() * CharacterCombatInfo.SECOND_ROW_SPEED);
    }
    
    @Test
    public void givenPlayableCharacterOnThirdRow_whenCreateCombatInfo_thenCreateCombatInfo() {
        // Create a playable character
        PlayableCharacter playableCharacter = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                                    TEST_CHARACTER_HEALTH,
                                                                    TEST_CHARACTER_SPEED);
        playableCharacter.setWeapon(WeaponFactory.createWeapon());
        
        // Create combat info
        CharacterCombatInfo combatInfo = new CharacterCombatInfo(playableCharacter,
                                                                 2,
                                                                 TEST_COMBAT_COLUMN,
                                                                 false);
        
        // Check
        assertThat(combatInfo.getCurrentAggro()).isEqualTo(
                playableCharacter.getWeapon().getInitialAggro() * CharacterCombatInfo.THIRD_ROW_AGGRO);
        assertThat(combatInfo.getSpeed()).isEqualTo(
                playableCharacter.getSpeed() * CharacterCombatInfo.THIRD_ROW_SPEED);
    }
    //endregion
    
    //region CombatProcessor
    @Test
    public void givenCharactersAndEnemies_whenCalculateTurnQueue_thenOrderBySpeed() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter fastCharacter = new PlayableCharacter("Fast", TEST_CHARACTER_HEALTH, 20);
        PlayableCharacter enemy = new PlayableCharacter("Enemy", TEST_CHARACTER_HEALTH, 15);
        PlayableCharacter slowCharacter = new PlayableCharacter("Slow", TEST_CHARACTER_HEALTH, 10);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, fastCharacter);
        map.getCombatTerrain().placeCharacter(0, 1, slowCharacter);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        
        // Verify the turn queue
        var turnQueue = processor.getTurnQueue().stream().toList();
        assertThat(turnQueue).hasSize(3);
        assertThat(turnQueue.get(0).getCharacter()).isEqualTo(fastCharacter);
        assertThat(turnQueue.get(1).getCharacter()).isEqualTo(enemy);
        assertThat(turnQueue.get(2).getCharacter()).isEqualTo(slowCharacter);
    }
    
    @Test
    public void givenCharactersAndEnemies_whenCalculateTurnQueue_thenSkipDeadAndHypnotized() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter deadCharacter = new PlayableCharacter("Character", 0, 20);
        PlayableCharacter deadEnemy = new PlayableCharacter("Enemy", 0, 10);
        PlayableCharacter hypnotizedEnemy = new PlayableCharacter("Enemy", TEST_CHARACTER_HEALTH, 15);
        hypnotizedEnemy.getChangedStats().updateStats(TEST_CHARACTER_HEALTH, true);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, deadCharacter);
        map.getCombatTerrain().placeCharacter(0, 1, deadEnemy);
        map.getCombatTerrain().placeEnemy(0, 0, hypnotizedEnemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        
        // Verify the turn queue
        var turnQueue = processor.getTurnQueue().stream().toList();
        assertThat(turnQueue).hasSize(0);
    }
    
    @Test
    public void givenCharactersAndEnemies_whenStartAutoCombat_thenProcessTurn() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcess combatProcess = combatService.startAutoCombat(TEST_USER_EMAIL);
        
        // Verify the turn queue
        PlayableCharacter[][] playerTerrain = combatService.getCombatStatus(TEST_USER_EMAIL).getPlayerTerrain();
        assertThat(playerTerrain[0][0]).isNotNull();
        assertThat(playerTerrain[0][0].getChangedStats().getHealth()).isEqualTo(
                character.getHealth() -
                enemy.getWeapon().getDamage() *
                (1 - character.getWeapon().getMeleeDefense() / 100));
        assertThat(playerTerrain[0][0].getChangedStats().isHypnotized()).isFalse();
        
        PlayableCharacter[][] enemyTerrain = combatService.getCombatStatus(TEST_USER_EMAIL).getEnemyTerrain();
        assertThat(enemyTerrain[0][0]).isNotNull();
        assertThat(enemyTerrain[0][0].getChangedStats().getHealth()).isEqualTo(
                enemy.getHealth() -
                character.getWeapon().getDamage() *
                (1 - enemy.getWeapon().getMeleeDefense() / 100));
        assertThat(enemyTerrain[0][0].getChangedStats().isHypnotized()).isFalse();
        
        assertThat(combatProcess.turns()).hasSize(2);
        TurnInfo first = combatProcess.turns().getFirst();
        assertThat(first.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(first.isEnemyTurn()).isFalse();
        TurnInfo last = combatProcess.turns().getLast();
        assertThat(last.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(last.isEnemyTurn()).isTrue();
    }
    
    @Test
    public void givenCharactersAndEnemies_whenEnemyDead_thenRemoveFromTerrain() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createMeleeCharacter(9999);
        character.getWeapon().setDamage(PlayableCharacterFactory.TEST_CHARACTER_HEALTH * 9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcess combatProcess = combatService.startAutoCombat(TEST_USER_EMAIL);
        
        // Verify the turn queue
        PlayableCharacter[][] playerTerrain = map.getCombatTerrain().getPlayerTerrain();
        assertThat(playerTerrain[0][0]).isNotNull();
        assertThat(playerTerrain[0][0].getChangedStats().getHealth()).isEqualTo(character.getHealth());
        assertThat(playerTerrain[0][0].getChangedStats().isHypnotized()).isFalse();
        
        PlayableCharacter[][] enemyTerrain = map.getCombatTerrain().getEnemyTerrain();
        assertThat(enemyTerrain[0][0]).isNull();
        
        assertThat(combatProcess.turns()).hasSize(1);
        TurnInfo first = combatProcess.turns().getFirst();
        assertThat(first.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(first.isEnemyTurn()).isFalse();
    }
    
    @Test
    public void givenCharactersAndEnemies_whenEnemyHypnotized_thenIsNotRemoveFromTerrain() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        character.getWeapon().setHypnotizationPower(100);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        enemy.setHypnotizationResistence(0);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcess combatProcess = combatService.startAutoCombat(TEST_USER_EMAIL);
        
        // Verify the turn queue
        PlayableCharacter[][] playerTerrain = map.getCombatTerrain().getPlayerTerrain();
        assertThat(playerTerrain[0][0]).isNotNull();
        assertThat(playerTerrain[0][0].getChangedStats().getHealth()).isEqualTo(character.getHealth());
        assertThat(playerTerrain[0][0].getChangedStats().isHypnotized()).isFalse();
        
        PlayableCharacter[][] enemyTerrain = map.getCombatTerrain().getEnemyTerrain();
        assertThat(enemyTerrain[0][0]).isNotNull();
        
        assertThat(combatProcess.turns()).hasSize(1);
        TurnInfo first = combatProcess.turns().getFirst();
        assertThat(first.action()).isEqualTo(CombatAction.HYPNOTIZE);
        assertThat(first.isEnemyTurn()).isFalse();
    }
    
    @Test
    public void givenCharactersAndEnemies_whenNoEnemyAlive_thenFinishCombat() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createMeleeCharacter(9999);
        character.getWeapon().setDamage(PlayableCharacterFactory.TEST_CHARACTER_HEALTH * 9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        combatService.startAutoCombat(TEST_USER_EMAIL);
        
        // Verify the turn queue
        assertThat(map.isInCombat()).isFalse();
    }
    
    @Test
    public void givenCharactersAndEnemies_whenNoAllyAlive_thenFinishCombat() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(9999);
        enemy.getWeapon().setDamage(PlayableCharacterFactory.TEST_CHARACTER_HEALTH * 9999);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        combatService.startAutoCombat(TEST_USER_EMAIL);
        
        // Verify the turn queue
        assertThat(map.isInCombat()).isFalse();
        assertThat(map.getInventory().getCharacters()).doesNotContainKey(character.getId());
    }
    
    @Test
    public void givenCharactersAndEnemies_whenFinishCombatWithEnemyHypnotized_thenAddAllCharacterToInventory() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createHypnotizerCharacter(999);
        character.getWeapon().setHypnotizationPower(100);
        PlayableCharacter melee = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        enemy.setHypnotizationResistence(0);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeCharacter(0, 1, melee);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        combatService.startAutoCombat(TEST_USER_EMAIL);
        
        assertThat(map.getInventory().getCharacters()).containsKey(character.getId());
        assertThat(map.getInventory().getCharacters()).containsKey(melee.getId());
        assertThat(map.getInventory().getCharacters()).containsKey(enemy.getId());
        PlayableCharacter addedEnemy = map.getInventory().getCharacters().get(enemy.getId());
        assertThat(addedEnemy.getChangedStats().getHealth()).isEqualTo(addedEnemy.getHealth());
        assertThat(addedEnemy.getChangedStats().isHypnotized()).isFalse();
    }
    
    @Test
    public void givenCharactersAndEnemies_whenFinishCombat_thenAddLootToInventory() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter character = PlayableCharacterFactory.createMeleeCharacter(9999);
        character.getWeapon().setDamage(PlayableCharacterFactory.TEST_CHARACTER_HEALTH * 9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        map.getCombatTerrain().placeCharacter(0, 0, character);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().setLoot(EnemyFactory.createEnemyPattern().loot(), 1234);
        
        // Execute the method
        combatService.startAutoCombat(TEST_USER_EMAIL);
        
        Collection<Item> items = map.getInventory().getItems().values();
        assertThat(items.stream().anyMatch(item -> item.getType() == ItemType.WEAPON)).isTrue();
        assertThat(items.stream().anyMatch(item -> item.getType() == ItemType.FOOD)).isTrue();
        assertThat(items.stream().anyMatch(item -> item.getType() == ItemType.POTION)).isTrue();
        assertThat(items.stream().anyMatch(item -> item.getType() == ItemType.MATERIAL)).isTrue();
    }
    //endregion
    
    //region MeleeCharacter
    @Test
    public void givenMeleeCharacter_whenProcessTurn_thenExecuteMeleeLogic() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter melee = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, melee);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(melee.getWeapon().getCooldown());
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double initialAggro = melee.getWeapon().getInitialAggro() * CharacterCombatInfo.FIRST_ROW_AGGRO;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + melee.getWeapon().getAggroGeneration());
        // Enemy: Health
        CharacterStatusChange enemyHealth = characterTurn.targetStatus().getFirst();
        assertThat(enemyHealth.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(enemyHealth.oldValue()).isEqualTo(TEST_CHARACTER_HEALTH);
        assertThat(enemyHealth.newValue()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                     melee.getWeapon().getDamage() *
                                                     (1 - enemy.getWeapon().getMeleeDefense() / 100));
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(melee.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + melee.getWeapon().getAggroGeneration());
        
        // Verify that the action is refrected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        assertThat(moreAggroEnemy.getHealth()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                         melee.getWeapon().getDamage() *
                                                         (1 - enemy.getWeapon().getMeleeDefense() / 100));
    }
    
    @Test
    public void givenMeleeCharacter_whenHaveCooldown_thenReduceCooldown() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter melee = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, melee);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.getTurnQueue().first().resetCooldown();
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.SKIP);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(CombatProcessor.NO_TARGET);
        assertThat(characterTurn.targetColumn()).isEqualTo(CombatProcessor.NO_TARGET);
        
        assertThat(characterTurn.characterStatus()).hasSize(1);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(melee.getWeapon().getCooldown());
        assertThat(cooldown.newValue()).isEqualTo(melee.getWeapon().getCooldown() - 1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(melee.getWeapon().getCooldown() - 1);
    }
    //endregion
    
    //region RangedCharacter
    @Test
    public void givenRangedCharacterOnFirstRow_whenProcessTurn_thenExecuteFirstRowRangedLogic() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.FIRST_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnSecondRow_whenProcessTurn_thenWillAttackSecondRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnSecondRow_whenNoEnemyOnSecondRow_thenWillAttackThirdRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.THIRD_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnSecondRow_whenNoEnemyOnSecondAndThirdRow_thenWillAttackFirstRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnThirdRow_whenProcessTurn_thenWillAttackThirdRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        PlayableCharacter enemyMoreAggro2 = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.THIRD_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        map.getCombatTerrain().placeEnemy(1, 0, enemyMoreAggro2);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnThirdRow_whenNoEnemyOnThirdRow_thenWillAttackSecondRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    @Test
    public void givenRangedCharacterOnThirdRow_whenNoEnemyOnThirdAndSecondRow_thenWillAttackFirstRowEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyRanged(processor, characterRow, characterColumn, targetRow, targetColumn, ranged, enemy);
    }
    
    private static void verifyRanged(CombatProcessor processor, int characterRow, int characterColumn,
                                     int targetRow, int targetColumn, PlayableCharacter ranged,
                                     PlayableCharacter target) {
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(ranged.getWeapon().getCooldown());
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double aggroModifier = characterRow == CombatProcessor.FIRST_ROW_INDEX ?
                               CharacterCombatInfo.FIRST_ROW_AGGRO :
                               (characterRow == CombatProcessor.SECOND_ROW_INDEX
                                ? CharacterCombatInfo.SECOND_ROW_AGGRO
                                : CharacterCombatInfo.THIRD_ROW_AGGRO);
        double initialAggro = ranged.getWeapon().getInitialAggro() * aggroModifier;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + ranged.getWeapon().getAggroGeneration());
        // Enemy: Health
        CharacterStatusChange enemyHealth = characterTurn.targetStatus().getFirst();
        assertThat(enemyHealth.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(enemyHealth.oldValue()).isEqualTo(TEST_CHARACTER_HEALTH);
        assertThat(enemyHealth.newValue()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                     ranged.getWeapon().getDamage() *
                                                     (1 - target.getWeapon().getRangedDefense() / 100));
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(ranged.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + ranged.getWeapon().getAggroGeneration());
        
        // Verify that the action is refrected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().getLast();
        assertThat(moreAggroEnemy.getHealth()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                         ranged.getWeapon().getDamage() *
                                                         (1 - target.getWeapon().getRangedDefense() / 100));
    }
    
    @Test
    public void givenRangedCharacter_whenHaveCooldown_thenReduceCooldown() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter ranged = PlayableCharacterFactory.createRangedCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, ranged);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.getTurnQueue().first().resetCooldown();
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.SKIP);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(CombatProcessor.NO_TARGET);
        assertThat(characterTurn.targetColumn()).isEqualTo(CombatProcessor.NO_TARGET);
        
        assertThat(characterTurn.characterStatus()).hasSize(1);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(ranged.getWeapon().getCooldown());
        assertThat(cooldown.newValue()).isEqualTo(ranged.getWeapon().getCooldown() - 1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(ranged.getWeapon().getCooldown() - 1);
    }
    //endregion
    
    //region TankCharacter
    @Test
    public void givenTankCharacter_whenProcessTurn_thenExecuteTankLogic() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter tank = PlayableCharacterFactory.createTankCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, tank);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(tank.getWeapon().getCooldown());
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double initialAggro = tank.getWeapon().getInitialAggro() * CharacterCombatInfo.FIRST_ROW_AGGRO;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + tank.getWeapon().getAggroGeneration());
        // Enemy: Health
        CharacterStatusChange enemyHealth = characterTurn.targetStatus().getFirst();
        assertThat(enemyHealth.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(enemyHealth.oldValue()).isEqualTo(TEST_CHARACTER_HEALTH);
        assertThat(enemyHealth.newValue()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                     tank.getWeapon().getDamage() *
                                                     (1 - enemy.getWeapon().getMeleeDefense() / 100));
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(tank.getWeapon().getCooldown());
        // Aggro is multiplied by 3 because the tank generates aggro when recives damage
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro +
                                                          tank.getWeapon().getAggroGeneration() * 3);
        boolean allEnemyGenerateAggro = processor.getTurnInfos()
                                                 .stream()
                                                 .skip(1)
                                                 .allMatch(x -> x.targetStatus()
                                                                 .stream()
                                                                 .anyMatch(y -> y.statChanged() ==
                                                                                CharacterStat.CURRENT_AGGRO));
        assertThat(allEnemyGenerateAggro).isTrue();
        
        // Verify that the action is refrected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        assertThat(moreAggroEnemy.getHealth()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                         tank.getWeapon().getDamage() *
                                                         (1 - enemy.getWeapon().getMeleeDefense() / 100));
    }
    
    @Test
    public void givenTankCharacter_whenHaveCooldown_thenReduceCooldown() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter tank = PlayableCharacterFactory.createTankCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, tank);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.getTurnQueue().first().resetCooldown();
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.SKIP);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(CombatProcessor.NO_TARGET);
        assertThat(characterTurn.targetColumn()).isEqualTo(CombatProcessor.NO_TARGET);
        
        assertThat(characterTurn.characterStatus()).hasSize(1);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(tank.getWeapon().getCooldown());
        assertThat(cooldown.newValue()).isEqualTo(tank.getWeapon().getCooldown() - 1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(tank.getWeapon().getCooldown() - 1);
    }
    
    @Test
    public void givenTankCharacter_whenAggroEnemyIsDead_thenWillAttackAnotherEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter tank = PlayableCharacterFactory.createTankCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, tank);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        moreAggroEnemy.damage(TEST_CHARACTER_HEALTH);
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
    }
    //endregion
    
    //region HealerCharacter
    @Test
    public void givenHealerCharacterOnFirstRow_whenProcessTurn_thenHealSameRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.FIRST_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally1);
        map.getCombatTerrain().placeCharacter(1, 1, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 2d);
        CharacterCombatInfo ally2Info = processor.getTurnQueue().stream().skip(2).findFirst().get();
        ally2Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        CharacterCombatInfo ally3Info = processor.getTurnQueue().stream().skip(3).findFirst().get();
        ally3Info.damage(TEST_CHARACTER_HEALTH / 1.7d);
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    @Test
    public void givenHealerCharacterOnSecondRow_whenProcessTurn_thenHealSameRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(0, 0, ally1);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        CharacterCombatInfo ally2Info = processor.getTurnQueue().stream().skip(2).findFirst().get();
        ally2Info.damage(TEST_CHARACTER_HEALTH / 2d);
        CharacterCombatInfo ally3Info = processor.getTurnQueue().stream().skip(3).findFirst().get();
        ally3Info.damage(TEST_CHARACTER_HEALTH / 1.7d);
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    @Test
    public void givenHealerCharacterOnSecondRow_whenSameRowNoHealableAlly_thenHealFirstRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally1);
        map.getCombatTerrain().placeCharacter(1, 1, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 2d);
        CharacterCombatInfo ally3Info = processor.getTurnQueue().stream().skip(3).findFirst().get();
        ally3Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    @Test
    public void givenHealerCharacterOnThirdRow_whenProcessTurn_thenHealSameRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.THIRD_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(0, 0, ally1);
        map.getCombatTerrain().placeCharacter(1, 0, ally2);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        CharacterCombatInfo ally2Info = processor.getTurnQueue().stream().skip(2).findFirst().get();
        ally2Info.damage(TEST_CHARACTER_HEALTH / 1.7d);
        CharacterCombatInfo ally3Info = processor.getTurnQueue().stream().skip(3).findFirst().get();
        ally3Info.damage(TEST_CHARACTER_HEALTH / 2d);
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    @Test
    public void givenHealerCharacterOnThirdRow_whenNoHealableAllyOnSameRow_thenHealSecondRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(0, 0, ally1);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        CharacterCombatInfo ally2Info = processor.getTurnQueue().stream().skip(2).findFirst().get();
        ally2Info.damage(TEST_CHARACTER_HEALTH / 2d);
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    @Test
    public void givenHealerCharacterOnThirdRow_whenNoHealableAllyOnSameRowAndSecondRow_thenHealFirstRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally1);
        map.getCombatTerrain().placeCharacter(1, 1, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 2d);
        processor.processTurn();
        
        verifyHealer(processor, characterRow, characterColumn, targetRow, targetColumn, healer);
    }
    
    private static void verifyHealer(CombatProcessor processor, int characterRow, int characterColumn, int targetRow,
                                     int targetColumn, PlayableCharacter healer) {
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.HEAL);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(healer.getWeapon().getCooldown());
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double aggroModifier = characterRow == CombatProcessor.FIRST_ROW_INDEX ?
                               CharacterCombatInfo.FIRST_ROW_AGGRO :
                               (characterRow == CombatProcessor.SECOND_ROW_INDEX
                                ? CharacterCombatInfo.SECOND_ROW_AGGRO
                                : CharacterCombatInfo.THIRD_ROW_AGGRO);
        double initialAggro = healer.getWeapon().getInitialAggro() * aggroModifier;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + healer.getWeapon().getAggroGeneration());
        // Ally: Health
        CharacterStatusChange allyHealth = characterTurn.targetStatus().getFirst();
        assertThat(allyHealth.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(allyHealth.oldValue()).isEqualTo(TEST_CHARACTER_HEALTH / 2d);
        assertThat(allyHealth.newValue()).isEqualTo(TEST_CHARACTER_HEALTH / 2d +
                                                    healer.getWeapon().getHealingPower());
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(healer.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + healer.getWeapon().getAggroGeneration());
        
        // Don't verify ally health, because it can be different depending on enemy attack
    }
    
    @Test
    public void givenHealerCharacter_whenNoAllyToHeal_thenAttackEnemies() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(0, 1, ally);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(healer.getWeapon().getCooldown());
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double initialAggro = healer.getWeapon().getInitialAggro() * CharacterCombatInfo.FIRST_ROW_AGGRO;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + healer.getWeapon().getDamage());
        // Enemy: Health
        CharacterStatusChange enemyHealth = characterTurn.targetStatus().getFirst();
        assertThat(enemyHealth.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(enemyHealth.oldValue()).isEqualTo(TEST_CHARACTER_HEALTH);
        assertThat(enemyHealth.newValue()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                     healer.getWeapon().getDamage() *
                                                     (1 - enemy.getWeapon().getRangedDefense() / 100));
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(healer.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + healer.getWeapon().getDamage());
        
        // Verify that the action is refrected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        assertThat(moreAggroEnemy.getHealth()).isEqualTo(TEST_CHARACTER_HEALTH -
                                                         healer.getWeapon().getDamage() *
                                                         (1 - enemy.getWeapon().getRangedDefense() / 100) -
                                                         ally.getWeapon().getDamage() *
                                                         (1 - enemy.getWeapon().getMeleeDefense() / 100));
    }
    
    @Test
    public void givenHealerCharacter_whenHaveCooldown_thenReduceCooldown() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = new PlayableCharacter("Healer", TEST_CHARACTER_HEALTH, 999);
        healer.setWeapon(WeaponFactory.createHealerWeapon());
        PlayableCharacter enemy = new PlayableCharacter("Enemy", TEST_CHARACTER_HEALTH, 15);
        enemy.setWeapon(WeaponFactory.createMeleeWeapon());
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.getTurnQueue().first().resetCooldown();
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.SKIP);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(CombatProcessor.NO_TARGET);
        assertThat(characterTurn.targetColumn()).isEqualTo(CombatProcessor.NO_TARGET);
        
        assertThat(characterTurn.characterStatus()).hasSize(1);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(healer.getWeapon().getCooldown());
        assertThat(cooldown.newValue()).isEqualTo(healer.getWeapon().getCooldown() - 1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(healer.getWeapon().getCooldown() - 1);
    }
    
    //endregion
    
    //region HypnotizerCharacter
    @Test
    public void givenHypnotizerCharacterOnFirstRow_whenProcessTurn_thenExecuteFirstRowHypnotizerLogic() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.FIRST_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnSecondRow_whenProcessTurn_thenWillHypnotizeSecondRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnSecondRow_whenNoEnemyOnSecondRow_thenWillHipnotizeThirdRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.THIRD_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnSecondRow_whenNoEnemyOnSecondAndThirdRow_thenWillHypnotizeFirstRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.SECOND_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnThirdRow_whenProcessTurn_thenWillHypnotizeThirdRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        PlayableCharacter enemyMoreAggro2 = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.THIRD_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        map.getCombatTerrain().placeEnemy(1, 0, enemyMoreAggro2);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnThirdRow_whenNoEnemyOnThirdRow_thenWillHypnotizeSecondRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(0, 0, enemyMoreAggro);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    @Test
    public void givenHypnotizerCharacterOnThirdRow_whenNoEnemyOnThirdAndSecondRow_thenWillHypnotizeFirstRowEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        verifyHypnotizer(processor, characterRow, characterColumn, targetRow, targetColumn, hypnotizer);
    }
    
    private static void verifyHypnotizer(CombatProcessor processor, int characterRow, int characterColumn,
                                         int targetRow, int targetColumn, PlayableCharacter hypnotizer) {
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.HYPNOTIZE);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(hypnotizer.getWeapon().getCooldown());
        
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double aggroModifier = characterRow == CombatProcessor.FIRST_ROW_INDEX ?
                               CharacterCombatInfo.FIRST_ROW_AGGRO :
                               (characterRow == CombatProcessor.SECOND_ROW_INDEX ?
                                CharacterCombatInfo.SECOND_ROW_AGGRO :
                                CharacterCombatInfo.THIRD_ROW_AGGRO);
        double initialAggro = hypnotizer.getWeapon().getInitialAggro() * aggroModifier;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + hypnotizer.getWeapon().getAggroGeneration());
        
        // Enemy: Hypnotized
        CharacterStatusChange enemyHypnotized = characterTurn.targetStatus().getFirst();
        assertThat(enemyHypnotized.statChanged()).isEqualTo(CharacterStat.HYPNOTIZED);
        assertThat(enemyHypnotized.oldValue()).isEqualTo(0);
        assertThat(enemyHypnotized.newValue()).isEqualTo(1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(hypnotizer.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + hypnotizer.getWeapon().getAggroGeneration());
        
        // Verify that the action is reflected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        assertThat(moreAggroEnemy.isHypnotized()).isTrue();
    }
    
    @Test
    public void givenHypnotizerCharacter_whenEnemyHaveMoreResistance_thenWillFailHypnotization() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        enemy.setHypnotizationResistence(hypnotizer.getWeapon().getHypnotizationPower());
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.FIRST_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.FIRST_ROW_INDEX;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.HYPNOTIZE);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(0);
        assertThat(cooldown.newValue()).isEqualTo(hypnotizer.getWeapon().getCooldown());
        
        // Character: Aggro
        CharacterStatusChange aggro = characterTurn.characterStatus().getLast();
        assertThat(aggro.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        double initialAggro = hypnotizer.getWeapon().getInitialAggro() * CharacterCombatInfo.FIRST_ROW_AGGRO;
        assertThat(aggro.oldValue()).isEqualTo(initialAggro);
        assertThat(aggro.newValue()).isEqualTo(initialAggro + hypnotizer.getWeapon().getAggroGeneration());
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(hypnotizer.getWeapon().getCooldown());
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + hypnotizer.getWeapon().getAggroGeneration());
        
        // Verify that the action is reflected in the enemy
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        assertThat(moreAggroEnemy.isHypnotized()).isFalse();
    }
    
    @Test
    public void givenHypnotizerCharacter_whenHaveCooldown_thenReduceCooldown() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        processor.getTurnQueue().first().resetCooldown();
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.SKIP);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(CombatProcessor.NO_TARGET);
        assertThat(characterTurn.targetColumn()).isEqualTo(CombatProcessor.NO_TARGET);
        
        assertThat(characterTurn.characterStatus()).hasSize(1);
        assertThat(characterTurn.targetStatus()).isEmpty();
        
        // Character: Cooldown
        CharacterStatusChange cooldown = characterTurn.characterStatus().getFirst();
        assertThat(cooldown.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(cooldown.oldValue()).isEqualTo(hypnotizer.getWeapon().getCooldown());
        assertThat(cooldown.newValue()).isEqualTo(hypnotizer.getWeapon().getCooldown() - 1);
        
        // Verify that the action is reflected in the character
        CharacterCombatInfo character = processor.getTurnQueue().first();
        assertThat(character.getCooldown()).isEqualTo(hypnotizer.getWeapon().getCooldown() - 1);
    }
    //endregion
    
    //region Target Dead Tests
    @Test
    public void givenAttackCharacter_whenAggroEnemyIsDead_thenWillAttackAnotherEnemy() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter melee = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = 0;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, melee);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        moreAggroEnemy.damage(TEST_CHARACTER_HEALTH);
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.ATTACK);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
        
        assertThat(characterTurn.characterStatus()).hasSize(2);
        assertThat(characterTurn.targetStatus()).hasSize(1);
    }
    
    @Test
    public void givenHealerCharacter_whenAllyIsDead_thenHealAnotherRowAlly() {
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter healer = PlayableCharacterFactory.createHealerCharacter(99999);
        PlayableCharacter ally1 = PlayableCharacterFactory.createMeleeCharacter(9999);
        PlayableCharacter ally2 = PlayableCharacterFactory.createMeleeCharacter(999);
        PlayableCharacter ally3 = PlayableCharacterFactory.createMeleeCharacter(99);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.THIRD_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = CombatProcessor.SECOND_ROW_INDEX;
        int targetColumn = 1;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, healer);
        map.getCombatTerrain().placeCharacter(0, 0, ally1);
        map.getCombatTerrain().placeCharacter(targetRow, targetColumn, ally2);
        map.getCombatTerrain().placeCharacter(2, 1, ally3);
        map.getCombatTerrain().placeEnemy(0, 0, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        // Damage the ally to simulate a healing
        CharacterCombatInfo ally1Info = processor.getTurnQueue().stream().skip(1).findFirst().get();
        ally1Info.damage(TEST_CHARACTER_HEALTH / 1.5d);
        CharacterCombatInfo ally2Info = processor.getTurnQueue().stream().skip(2).findFirst().get();
        ally2Info.damage(TEST_CHARACTER_HEALTH / 1.7d);
        CharacterCombatInfo ally3Info = processor.getTurnQueue().stream().skip(3).findFirst().get();
        ally3Info.damage(TEST_CHARACTER_HEALTH);
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.HEAL);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
    }
    
    @Test
    public void givenHypnotizerCharacter_whenAggroEnemyIsDead_thenWillHypnotizeAnotherEnemy() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        // Create an empty game map
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        map.setInCombat(true);
        
        // Create characters and enemies
        PlayableCharacter hypnotizer = PlayableCharacterFactory.createHypnotizerCharacter(9999);
        PlayableCharacter enemy = PlayableCharacterFactory.createMeleeCharacter(15);
        PlayableCharacter enemyMoreAggro = PlayableCharacterFactory.createMeleeCharacter(15, 9999);
        
        // Place characters and enemies on the combat terrain
        int characterRow = CombatProcessor.FIRST_ROW_INDEX;
        int characterColumn = 0;
        int targetRow = 0;
        int targetColumn = 0;
        map.getCombatTerrain().placeCharacter(characterRow, characterColumn, hypnotizer);
        map.getCombatTerrain().placeEnemy(targetRow, targetColumn, enemy);
        map.getCombatTerrain().placeEnemy(0, 1, enemyMoreAggro);
        
        // Execute the method
        CombatProcessor processor = new CombatProcessor(map.getCombatTerrain());
        CharacterCombatInfo moreAggroEnemy = processor.getTurnQueue().last();
        moreAggroEnemy.damage(TEST_CHARACTER_HEALTH);
        processor.processTurn();
        
        // Verify the turn info is correct
        TurnInfo characterTurn = processor.getTurnInfos().getFirst();
        assertThat(characterTurn.action()).isEqualTo(CombatAction.HYPNOTIZE);
        assertThat(characterTurn.row()).isEqualTo(characterRow);
        assertThat(characterTurn.column()).isEqualTo(characterColumn);
        assertThat(characterTurn.isEnemyTurn()).isEqualTo(false);
        assertThat(characterTurn.targetRow()).isEqualTo(targetRow);
        assertThat(characterTurn.targetColumn()).isEqualTo(targetColumn);
    }
    //endregion
}
