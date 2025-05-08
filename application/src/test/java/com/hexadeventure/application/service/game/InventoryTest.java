package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.ItemFactory;
import com.hexadeventure.application.service.common.MapFactory;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

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
    }
    
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
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        
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
        
        assertThatExceptionOfType(NotEnoughtResourcesException.class)
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
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - 1);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - 1));
        assertThat(map.getInventory().getItems()).hasSize(craftableCount);
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(1);
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
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - 1);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - 1));
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_FOOD_NAME).getCount())
                .isEqualTo(craftableCount - 1);
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
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - 1);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - 1));
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_POTION_NAME).getCount())
                .isEqualTo(craftableCount - 1);
    }
    
    public void givenMaterialRecipe_whenCraft_thenCraftTheItems() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(MapFactory.EMPTY_MAP_ID);
        
        GameMap map = MapFactory.createEmptyGameMap(gameMapRepository, aStarPathfinder, settingsImporter);
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        int craftableCount = 10;
        map.getInventory().addItem(materials.get(ItemFactory.TEST_MATERIAL_TYPE), craftableCount);
        
        int recipeIndex = ItemFactory.TEST_MATERIAL_RECIPE_INDEX;
        inventoryService.craft(UserFactory.EMAIL, recipeIndex, craftableCount - 1);
        
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - (craftableCount - 1));
        assertThat(map.getInventory().getItems().get(ItemFactory.TEST_MATERIAL_TYPE.toString()).getCount())
                .isEqualTo(craftableCount - 1);
    }
}
