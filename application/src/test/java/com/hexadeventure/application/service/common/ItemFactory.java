package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;
import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ItemFactory {
    public static final String TEST_WEAPON_NAME = "Sword";
    public static final String TEST_FOOD_NAME = "Apple";
    public static final String TEST_POTION_NAME = "Health Potion";
    public static final ResourceType TEST_MATERIAL_TYPE = ResourceType.WOOD;
    
    public static void setupSettingsImporter(SettingsImporter settingsImporter) {
        when(settingsImporter.importInitialResources()).thenReturn(getInitialResources());
        
        Map<String, WeaponSetting> weaponsCache = new HashMap<>();
        Map<String, Food> foodsCache = new HashMap<>();
        Map<String, Potion> potionsCache = new HashMap<>();
        Map<ResourceType, Material> materialsCache = new HashMap<>();
        
        WeaponSetting weaponSetting = new WeaponSetting(TEST_WEAPON_NAME, 1, 0, 1, WeaponType.MELEE, 1, 1,
                                                        1, 1, 1, 1,
                                                        1, 1, AggroGenType.ATTACK, 1,
                                                        1, 1, 1, 1,
                                                        1, 1, 1);
        weaponsCache.put(TEST_WEAPON_NAME, weaponSetting);
        
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
        
        when(settingsImporter.importWeaponsByTypeAndThreshold(anyInt())).thenReturn(WeaponFactory.createWeaponsSettings());
        EnemyPattern[] enemyPatterns = {EnemyFactory.createEnemyPattern()};
        when(settingsImporter.importEnemyPatterns(anyDouble())).thenReturn(enemyPatterns);
        when(settingsImporter.importBossPatterns()).thenReturn(enemyPatterns);
    }
    
    private static InitialResources getInitialResources() {
        InitialStringIdResourceData[] initialWeapons = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_WEAPON_NAME, 1)};
        InitialStringIdResourceData[] initialFoods = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_FOOD_NAME, 1)};
        InitialStringIdResourceData[] initialPotions = new InitialStringIdResourceData[]{
                new InitialStringIdResourceData(TEST_POTION_NAME, 1)};
        InitialResourceTypeIdResourceData[] initialMaterials = new InitialResourceTypeIdResourceData[]{
                new InitialResourceTypeIdResourceData(TEST_MATERIAL_TYPE, 1)};
        
        InitialResources initialResources = new InitialResources();
        initialResources.setInitialWeapons(initialWeapons);
        initialResources.setInitialFoods(initialFoods);
        initialResources.setInitialPotions(initialPotions);
        initialResources.setInitialMaterials(initialMaterials);
        return initialResources;
    }
}
