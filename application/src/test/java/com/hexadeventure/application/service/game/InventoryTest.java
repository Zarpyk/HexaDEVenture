package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.*;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class InventoryTest {
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private static final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private static final SettingsImporter settingsImporter = mock(SettingsImporter.class);
    private final InventoryService inventoryService = new InventoryService(userRepository,
                                                                           gameMapRepository,
                                                                           settingsImporter);
    
    @BeforeEach
    public void beforeEach() {
        ItemFactory.setupSettingsImporter(settingsImporter);
        
        // Reset verify mocks
        Mockito.reset(gameMapRepository);
    }
    
    //region GetRecipes
    @Test
    public void givenInvalidPage_whenGetRecipes_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(InvalidSearchException.class)
                .isThrownBy(() -> inventoryService.getRecipes(UserFactory.EMAIL, -1, 10));
    }
    
    @Test
    public void givenInvalidSize_whenGetRecipes_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(InvalidSearchException.class)
                .isThrownBy(() -> inventoryService.getRecipes(UserFactory.EMAIL, 0, -1));
    }
    
    @Test
    public void givenPageAndSizeMoreThanAllRecipe_whenGetRecipes_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(InvalidSearchException.class)
                .isThrownBy(() -> inventoryService.getRecipes(UserFactory.EMAIL,
                                                              1,
                                                              ItemFactory.TEST_RECIPE_COUNT +
                                                              ItemFactory.TEST_EXTRA_RECIPE_COUNT + 1));
        assertThatExceptionOfType(InvalidSearchException.class)
                .isThrownBy(() -> inventoryService.getRecipes(UserFactory.EMAIL,
                                                              2,
                                                              ItemFactory.TEST_RECIPE_COUNT));
    }
    
    @Test
    public void givenPageAndSize_whenGetRecipes_thenReturnRecipes() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        int size = 10;
        for (int page = 1; page <= 2; page++) {
            Recipe[] recipes = inventoryService.getRecipes(UserFactory.EMAIL, page, size);
            for (int i = 0; i < size; i++) {
                assertThat(recipes[i].getResultID()).isEqualTo(ItemFactory.TEST_RECIPE_NAME +
                                                               (i + (size * (page - 1))));
                assertThat(recipes[i].getCraftableAmount()).isEqualTo(0);
            }
        }
    }
    
    @Test
    public void givenItems_whenGetRecipes_thenReturnRecipesWithCraftableAmount() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int size = 10;
        for (int page = 1; page <= 2; page++) {
            Recipe[] recipes = inventoryService.getRecipes(UserFactory.EMAIL, page, size);
            for (int i = 0; i < size; i++) {
                assertThat(recipes[i].getResultID()).isEqualTo(ItemFactory.TEST_RECIPE_NAME +
                                                               (i + (size * (page - 1))));
                assertThat(recipes[i].getCraftableAmount()).isEqualTo(craftableCount);
            }
        }
    }
    
    @Test
    public void givenNoStartGameUser_whenGetRecipes_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.getRecipes(UserFactory.EMAIL, 1, 10));
    }
    //endregion
    
    //region Craft
    @Test
    public void givenNegativeCount_whenCraft_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(SizeException.class)
                .isThrownBy(() -> inventoryService.craft(UserFactory.EMAIL, 0, -1));
    }
    
    @Test
    public void givenInvalidRecipeIndex_whenCraft_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(InvalidRecipeException.class)
                .isThrownBy(() -> inventoryService.craft(UserFactory.EMAIL, -1, 10));
        assertThatExceptionOfType(InvalidRecipeException.class)
                .isThrownBy(() -> inventoryService.craft(UserFactory.EMAIL,
                                                         ItemFactory.TEST_RECIPE_COUNT +
                                                         ItemFactory.TEST_EXTRA_RECIPE_COUNT,
                                                         10));
    }
    
    @Test
    public void givenNoStartGameUser_whenCraft_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.craft(UserFactory.EMAIL, 0, 10));
    }
    
    @Test
    public void givenInvalidCount_whenCraft_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        assertThatExceptionOfType(NotEnoughResourcesException.class)
                .isThrownBy(() -> inventoryService.craft(UserFactory.EMAIL, 0, craftableCount + 1));
    }
    
    @Test
    public void givenWeaponRecipe_whenCraft_thenCraftTheItems() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int recipeIndex = ItemFactory.TEST_WEAPON_RECIPE_INDEX;
        int remainMaterial = 1;
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - remainMaterial);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - remainMaterial));
        assertThat(map.getInventory().getItems()).hasSize(craftableCount);
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(remainMaterial);
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenFoodRecipe_whenCraft_thenCraftTheItems() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int recipeIndex = ItemFactory.TEST_FOOD_RECIPE_INDEX;
        int remainMaterial = 1;
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - remainMaterial);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - remainMaterial));
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_FOOD_NAME).getCount())
                .isEqualTo(craftableCount - remainMaterial);
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenPotionRecipe_whenCraft_thenCraftTheItems() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int recipeIndex = ItemFactory.TEST_POTION_RECIPE_INDEX;
        int remainMaterial = 1;
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - remainMaterial);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - remainMaterial));
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_HEALTH_POTION_NAME).getCount())
                .isEqualTo(craftableCount - remainMaterial);
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenMaterialRecipe_whenCraft_thenCraftTheItems() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int recipeIndex = ItemFactory.TEST_MATERIAL_RECIPE_INDEX;
        int remainMaterial = 1;
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - remainMaterial);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo((craftableCount - remainMaterial) * ItemFactory.TEST_MATERIAL_RECIPE_RESULT_COUNT +
                           remainMaterial);
        
        verify(gameMapRepository, times(1)).save(map);
    }
    //endregion
    
    //region GetInventory
    @Test
    public void givenNoStartGameUser_whenGetInventory_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.getInventory(UserFactory.EMAIL));
    }
    
    @Test
    public void givenUser_whenGetInventory_thenReturnInventory() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
        assertThat(inventoryService.getInventory(UserFactory.EMAIL)).isEqualTo(map.getInventory());
    }
    //endregion
    
    //region EquipWeapon
    @Test
    public void givenNoStartGameUser_whenEquipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        map.getInventory().addCharacter(character);
        map.getInventory().addItem(weapon, 1);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.equipWeapon(UserFactory.EMAIL,
                                                               character.getId(),
                                                               weapon.getId()));
    }
    
    @Test
    public void givenCharacterIdAndWeaponId_whenEquipWeapon_thenEquipWeaponToCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        map.getInventory().addCharacter(character);
        map.getInventory().addItem(weapon, 1);
        
        inventoryService.equipWeapon(UserFactory.EMAIL, character.getId(), weapon.getId());
        
        assertThat(map.getInventory().getCharacters().get(character.getId()).getWeapon()).isEqualTo(weapon);
        assertThat(map.getInventory().getItems().get(weapon.getId())).isNull();
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenInvalidCharacterId_whenEquipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        map.getInventory().addCharacter(character);
        map.getInventory().addItem(weapon, 1);
        
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.equipWeapon(UserFactory.EMAIL, "", weapon.getId()));
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.equipWeapon(UserFactory.EMAIL, null, weapon.getId()));
    }
    
    @Test
    public void givenInvalidWeaponId_whenEquipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        map.getInventory().addCharacter(character);
        map.getInventory().addItem(weapon, 1);
        
        assertThatExceptionOfType(InvalidItemException.class)
                .isThrownBy(() -> inventoryService.equipWeapon(UserFactory.EMAIL, character.getId(), ""));
        assertThatExceptionOfType(InvalidItemException.class)
                .isThrownBy(() -> inventoryService.equipWeapon(UserFactory.EMAIL, character.getId(), null));
    }
    //endregion
    
    //region UnequipWeapon
    @Test
    public void givenNoStartGameUser_whenUnequipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        character.setWeapon(weapon);
        map.getInventory().addCharacter(character);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.unequipWeapon(UserFactory.EMAIL, character.getId()));
    }
    
    @Test
    public void givenCharacterId_whenUnequipWeapon_thenUnequipWeaponToCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        character.setWeapon(weapon);
        map.getInventory().addCharacter(character);
        
        inventoryService.unequipWeapon(UserFactory.EMAIL, character.getId());
        
        assertThat(map.getInventory().getCharacters().get(character.getId()).getWeapon().getName())
                .isEqualTo(Weapon.DEFAULT_WEAPON.getName());
        assertThat(map.getInventory().getItems().get(weapon.getId()).getCount()).isEqualTo(1);
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenInvalidCharacterId_whenUnequipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        character.setWeapon(weapon);
        map.getInventory().addCharacter(character);
        
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.unequipWeapon(UserFactory.EMAIL, ""));
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.unequipWeapon(UserFactory.EMAIL, null));
    }
    
    @Test
    public void givenCharacterWithoutWeapon_whenUnequipWeapon_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        map.getInventory().addCharacter(character);
        
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.unequipWeapon(UserFactory.EMAIL, character.getId()));
    }
    //endregion
    
    //region UseItem
    @Test
    public void givenNoStartGameUser_whenUseItem_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Food food = settingsImporter.importFoods().get(ItemFactory.TEST_FOOD_NAME);
        map.getInventory().addCharacter(character);
        map.getInventory().addItem(food, 1);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> inventoryService.useItem(UserFactory.EMAIL,
                                                           character.getId(),
                                                           food.getId()));
    }
    
    @Test
    public void givenCharacterIdAndFoodId_whenUseItem_thenHealCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Food food = settingsImporter.importFoods().get(ItemFactory.TEST_FOOD_NAME);
        map.getInventory().addCharacter(character);
        character.getChangedStats().updateStats(PlayableCharacterFactory.TEST_CHARACTER_HEALTH / 2d, false);
        map.getInventory().addItem(food, 1);
        
        inventoryService.useItem(UserFactory.EMAIL, character.getId(), food.getId());
        
        assertThat(map.getInventory().getCharacters().get(character.getId()).getChangedStats().getHealth())
                .isEqualTo(PlayableCharacterFactory.TEST_CHARACTER_HEALTH / 2d + food.getHealthPoints());
        
        verify(gameMapRepository, times(1)).save(map);
    }
    
    @Test
    public void givenCharacterIdAndPotionId_whenUseItem_thenBoostCharacter() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Map<String, Potion> potions = settingsImporter.importPotions();
        map.getInventory().addCharacter(character);
        
        // Add potions to inventory and use them
        Potion potion = potions.get(ItemFactory.TEST_HEALTH_POTION_NAME);
        map.getInventory().addItem(potion, 1);
        inventoryService.useItem(UserFactory.EMAIL, character.getId(), potion.getId());
        potion = potions.get(ItemFactory.TEST_SPEED_POTION_NAME);
        map.getInventory().addItem(potion, 1);
        inventoryService.useItem(UserFactory.EMAIL, character.getId(), potion.getId());
        potion = potions.get(ItemFactory.TEST_STRENGTH_POTION_NAME);
        map.getInventory().addItem(potion, 1);
        inventoryService.useItem(UserFactory.EMAIL, character.getId(), potion.getId());
        potion = potions.get(ItemFactory.TEST_DEFENSE_POTION_NAME);
        map.getInventory().addItem(potion, 1);
        inventoryService.useItem(UserFactory.EMAIL, character.getId(), potion.getId());
        
        // Check if the character has the boosted stats
        PlayableCharacter playableCharacter = map.getInventory().getCharacters().get(character.getId());
        assertThat(playableCharacter.getChangedStats().getBoostHealth()).isEqualTo(ItemFactory.TEST_POTION_POWER);
        assertThat(playableCharacter.getChangedStats().getBoostSpeed()).isEqualTo(ItemFactory.TEST_POTION_POWER);
        assertThat(playableCharacter.getChangedStats().getBoostStrength()).isEqualTo(ItemFactory.TEST_POTION_POWER);
        assertThat(playableCharacter.getChangedStats().getBoostDefense()).isEqualTo(ItemFactory.TEST_POTION_POWER);
        
        verify(gameMapRepository, times(4)).save(map);
    }
    
    @Test
    public void givenInvalidCharacterId_whenUseItem_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Food food = settingsImporter.importFoods().get(ItemFactory.TEST_FOOD_NAME);
        map.getInventory().addCharacter(character);
        character.getChangedStats().updateStats(PlayableCharacterFactory.TEST_CHARACTER_HEALTH / 2d, false);
        map.getInventory().addItem(food, 1);
        
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.useItem(UserFactory.EMAIL, "", food.getId()));
        assertThatExceptionOfType(InvalidCharacterException.class)
                .isThrownBy(() -> inventoryService.useItem(UserFactory.EMAIL, null, food.getId()));
    }
    
    @Test
    public void givenInvalidItemId_whenUseItem_thenThrowException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        PlayableCharacter character = PlayableCharacterFactory.createNoWeaponCharacter(0);
        Food food = settingsImporter.importFoods().get(ItemFactory.TEST_FOOD_NAME);
        map.getInventory().addCharacter(character);
        character.getChangedStats().updateStats(PlayableCharacterFactory.TEST_CHARACTER_HEALTH / 2d, false);
        map.getInventory().addItem(food, 1);
        
        assertThatExceptionOfType(InvalidItemException.class)
                .isThrownBy(() -> inventoryService.useItem(UserFactory.EMAIL, character.getId(), ""));
        assertThatExceptionOfType(InvalidItemException.class)
                .isThrownBy(() -> inventoryService.useItem(UserFactory.EMAIL, character.getId(), null));
    }
    //endregion
}
