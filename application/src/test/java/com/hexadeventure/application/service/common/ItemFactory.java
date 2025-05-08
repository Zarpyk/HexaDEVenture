package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialCharacter;
import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public class ItemFactory {
    public static final String TEST_WEAPON_NAME = "Sword";
    public static final String TEST_RANDOM_WEAPON_NAME = "Random Sword";
    public static final String TEST_FOOD_NAME = "Apple";
    public static final String TEST_POTION_NAME = "Health Potion";
    public static final ResourceType TEST_MATERIAL_TYPE = ResourceType.WOOD;
    
    public static final String TEST_RECIPE_NAME = "TestRecipe";
    public static final int TEST_RECIPE_COUNT = 20;
    public static final int TEST_EXTRA_RECIPE_COUNT = 4;
    public static final int TEST_WEAPON_RECIPE_INDEX = TEST_RECIPE_COUNT;
    public static final int TEST_FOOD_RECIPE_INDEX = TEST_RECIPE_COUNT + 1;
    public static final int TEST_POTION_RECIPE_INDEX = TEST_RECIPE_COUNT + 2;
    public static final int TEST_MATERIAL_RECIPE_INDEX = TEST_RECIPE_COUNT + 3;
    
    public static void setupSettingsImporter(SettingsImporter settingsImporter) {
        when(settingsImporter.importInitialResources()).thenReturn(getInitialResources());
        
        Map<String, WeaponSetting> weaponsCache = new HashMap<>();
        Map<String, Food> foodsCache = new HashMap<>();
        Map<String, Potion> potionsCache = new HashMap<>();
        Map<ResourceType, Material> materialsCache = new HashMap<>();
        
        WeaponSetting weaponSetting = new WeaponSetting(TEST_WEAPON_NAME, 1,
                                                        0, 1, WeaponType.MELEE,
                                                        1, 1,
                                                        1, 1,
                                                        1, 1,
                                                        1, 1, AggroGenType.ATTACK,
                                                        1,
                                                        1, 1,
                                                        1,
                                                        1, 1,
                                                        1, 1);
        weaponsCache.put(TEST_WEAPON_NAME, weaponSetting);
        WeaponSetting randomWeapon = new WeaponSetting(TEST_RANDOM_WEAPON_NAME, 1,
                                                       0, 1,
                                                       WeaponType.MELEE,
                                                       1, Integer.MAX_VALUE - 1,
                                                       1, Integer.MAX_VALUE - 1,
                                                       1, Integer.MAX_VALUE - 1,
                                                       1, Integer.MAX_VALUE - 1,
                                                       AggroGenType.ATTACK,
                                                       1,
                                                       1, Integer.MAX_VALUE - 1,
                                                       1,
                                                       1, Integer.MAX_VALUE - 1,
                                                       1, Integer.MAX_VALUE - 1);
        weaponsCache.put(TEST_RANDOM_WEAPON_NAME, randomWeapon);
        
        Food food = new Food(TEST_FOOD_NAME, 1, 1);
        foodsCache.put(TEST_FOOD_NAME, food);
        
        Potion potion = new Potion(TEST_POTION_NAME, 1, PotionType.HEALING);
        potionsCache.put(TEST_POTION_NAME, potion);
        
        Material material = new Material("Wood", 1, TEST_MATERIAL_TYPE);
        materialsCache.put(TEST_MATERIAL_TYPE, material);
        
        when(settingsImporter.importWeapons()).thenReturn(weaponsCache);
        when(settingsImporter.importFoods()).thenReturn(foodsCache);
        when(settingsImporter.importPotions()).thenReturn(potionsCache);
        when(settingsImporter.importMaterials()).thenReturn(materialsCache);
        
        when(settingsImporter.importWeaponsByTypeAndThreshold(anyDouble()))
                .thenReturn(WeaponFactory.createWeaponsSettings());
        EnemyPattern[] enemyPatterns = {EnemyFactory.createEnemyPattern()};
        when(settingsImporter.importEnemyPatterns(anyDouble())).thenReturn(enemyPatterns);
        when(settingsImporter.importBossPatterns()).thenReturn(enemyPatterns);
        
        when(settingsImporter.importRecipes()).thenReturn(returnRecipes());
    }
    
    @SuppressWarnings("ExtractMethodRecommender")
    private static InitialResources getInitialResources() {
        InitialCharacter[] initialCharacters = new InitialCharacter[]{
                new InitialCharacter("Hero", 1, 1, 1, 1)};
        InitialStringIdResourceData[] initialWeapons = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_WEAPON_NAME, 1)};
        InitialStringIdResourceData[] initialFoods = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_FOOD_NAME, 1)};
        InitialStringIdResourceData[] initialPotions = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_POTION_NAME, 1)};
        InitialResourceTypeIdResourceData[] initialMaterials = new InitialResourceTypeIdResourceData[]{
                new InitialResourceTypeIdResourceData(TEST_MATERIAL_TYPE, 1)};
        
        InitialResources initialResources = new InitialResources();
        initialResources.setInitialCharacters(initialCharacters);
        initialResources.setInitialWeapons(initialWeapons);
        initialResources.setInitialFoods(initialFoods);
        initialResources.setInitialPotions(initialPotions);
        initialResources.setInitialMaterials(initialMaterials);
        return initialResources;
    }
    
    private static List<Recipe> returnRecipes() {
        RecipeResource[] recipeResources = new RecipeResource[]{new RecipeResource(TEST_MATERIAL_TYPE.toString(),
                                                                                   ItemType.MATERIAL,
                                                                                   1)};
        
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < TEST_RECIPE_COUNT; i++) {
            String recipeName = TEST_RECIPE_NAME + i;
            Recipe recipe = new Recipe(recipeName, ItemType.WEAPON, 1, recipeResources);
            recipes.add(recipe);
        }
        
        // Add real recipes
        Recipe recipe = new Recipe(TEST_RANDOM_WEAPON_NAME, ItemType.WEAPON, 1, recipeResources);
        recipes.add(recipe);
        recipe = new Recipe(TEST_FOOD_NAME, ItemType.FOOD, 1, recipeResources);
        recipes.add(recipe);
        recipe = new Recipe(TEST_POTION_NAME, ItemType.POTION, 1, recipeResources);
        recipes.add(recipe);
        recipe = new Recipe(TEST_MATERIAL_TYPE.toString(), ItemType.MATERIAL, 1, recipeResources);
        recipes.add(recipe);
        
        return recipes;
    }
}
