package com.hexadeventure.adapter.out.settings;

import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.resources.ResourceType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingsImporterTest {
    private final SettingsImporterAdapter settingsImporter;
    
    public SettingsImporterTest() {
        settingsImporter = new SettingsImporterAdapter();
    }
    
    @Test
    public void givenJson_whenImportWeapons_thenReturnWeaponDataList() {
        Map<String, WeaponSetting> weaponsData = settingsImporter.importWeapons();
        
        assertThat(weaponsData.size()).isPositive();
        Optional<WeaponSetting> weapon = weaponsData.values().stream().findFirst();
        assertThat(weapon).isPresent();
        
        WeaponSetting weaponSetting = weapon.get();
        assertThat(weaponSetting.name()).isNotBlank();
        assertThat(weaponSetting.skin()).isPositive();
        assertThat(weaponSetting.weaponType()).isNotNull();
        assertThat(weaponSetting.minDamage()).isPositive();
        assertThat(weaponSetting.maxDamage()).isPositive();
        assertThat(weaponSetting.minMeleeDefense()).isPositive();
        assertThat(weaponSetting.maxMeleeDefense()).isPositive();
        assertThat(weaponSetting.minRangedDefense()).isPositive();
        assertThat(weaponSetting.maxRangedDefense()).isPositive();
        assertThat(weaponSetting.minCooldown()).isPositive();
        assertThat(weaponSetting.maxCooldown()).isPositive();
        assertThat(weaponSetting.aggroGenType()).isNotNull();
        assertThat(weaponSetting.extraAggroGeneration()).isPositive();
        assertThat(weaponSetting.minAggroGeneration()).isPositive();
        assertThat(weaponSetting.maxAggroGeneration()).isPositive();
        assertThat(weaponSetting.initialAggro()).isPositive();
        assertThat(weaponSetting.minHealingPower()).isPositive();
        assertThat(weaponSetting.maxHealingPower()).isPositive();
        assertThat(weaponSetting.minHypnotizationPower()).isPositive();
        assertThat(weaponSetting.maxHypnotizationPower()).isPositive();
    }
    
    @Test
    public void givenThreshold_whenImportWeaponsByTypeWithThreshold_thenReturnWeaponsOnTheRange() {
        Map<WeaponType, List<WeaponSetting>> weapons = settingsImporter.importWeaponsByTypeAndThreshold(0.5);
        assertThat(weapons.get(WeaponType.MELEE)).hasSize(1);
        
        weapons = settingsImporter.importWeaponsByTypeAndThreshold(1);
        assertThat(weapons.get(WeaponType.MELEE)).hasSize(1);
    }
    
    @Test
    public void givenJson_whenImportFoods_thenReturnFoodsList() {
        Map<String, Food> foods = settingsImporter.importFoods();
        
        assertThat(foods.size()).isPositive();
        Optional<Food> food = foods.values().stream().findFirst();
        assertThat(food).isPresent();
        
        Food foodObj = food.get();
        assertThat(foodObj.getName()).isNotBlank();
        assertThat(foodObj.getSkin()).isPositive();
        assertThat(foodObj.getHealthPoints()).isPositive();
    }
    
    @Test
    public void givenJson_whenImportPotions_thenReturnPotionsList() {
        Map<String, Potion> potions = settingsImporter.importPotions();
        
        assertThat(potions.size()).isPositive();
        Optional<Potion> potion = potions.values().stream().findFirst();
        assertThat(potion).isPresent();
        
        Potion potionObj = potion.get();
        assertThat(potionObj.getName()).isNotBlank();
        assertThat(potionObj.getSkin()).isPositive();
        assertThat(potionObj.getPotionPower()).isPositive();
        assertThat(potionObj.getPotionType()).isNotNull();
    }
    
    @Test
    public void givenJson_whenImportMaterials_thenReturnMaterialsList() {
        Map<ResourceType, Material> materials = settingsImporter.importMaterials();
        
        assertThat(materials.size()).isPositive();
        Optional<Material> material = materials.values().stream().findFirst();
        assertThat(material).isPresent();
        
        Material materialObj = material.get();
        assertThat(materialObj.getName()).isNotBlank();
        assertThat(materialObj.getSkin()).isPositive();
        assertThat(materialObj.getMaterialType()).isNotNull();
    }
    
    @Test
    public void givenJson_whenImportInitialResources_thenReturnInitialResources() {
        InitialResources initialResources = settingsImporter.importInitialResources();
        
        assertThat(initialResources).isNotNull();
        assertThat(initialResources.getInitialCharacters().length).isPositive();
        assertThat(initialResources.getInitialWeapons().length).isPositive();
        assertThat(initialResources.getInitialFoods().length).isPositive();
        assertThat(initialResources.getInitialPotions().length).isPositive();
        assertThat(initialResources.getInitialMaterials().length).isPositive();
    }
    
    @Test
    public void givenJson_whenImportEnemyPatterns_thenReturnEnemyPatterns() {
        EnemyPattern[] enemyPatterns = settingsImporter.importEnemyPatterns(1);
        
        assertThat(enemyPatterns).isNotNull();
        assertThat(enemyPatterns.length).isPositive();
        
        Optional<EnemyPattern> pattern = Arrays.stream(enemyPatterns).findFirst();
        assertThat(pattern).isPresent();
        
        EnemyPattern enemyPattern = pattern.get();
        
        // 0 only for testing, can be -1 to indicate that is a boss
        assertThat(enemyPattern.minThreshold()).isGreaterThanOrEqualTo(0);
        
        assertThat(enemyPattern.enemies()).isNotEmpty();
        assertThat(enemyPattern.enemies().length).isEqualTo(3);
        assertThat(enemyPattern.enemies()[0].length).isEqualTo(4);
    }
    
    @Test
    public void givenThreshold_whenGetEnemyPatterns_thenReturnAllPaternsLowerOrEqualThanThreshold() {
        EnemyPattern[] enemyPatterns = settingsImporter.importEnemyPatterns(0.5f);
        assertThat(enemyPatterns.length).isEqualTo(2);
        
        enemyPatterns = settingsImporter.importEnemyPatterns(0.4f);
        assertThat(enemyPatterns.length).isEqualTo(1);
        
        enemyPatterns = settingsImporter.importEnemyPatterns(0);
        assertThat(enemyPatterns.length).isEqualTo(0);
    }
    
    @Test
    public void givenJson_whenImportEnemyPatterns_thenHaveLoot() {
        EnemyPattern[] enemyPatterns = settingsImporter.importEnemyPatterns(1);
        
        assertThat(enemyPatterns).isNotNull();
        assertThat(enemyPatterns.length).isPositive();
        
        Optional<EnemyPattern> pattern = Arrays.stream(enemyPatterns).findFirst();
        assertThat(pattern).isPresent();
        
        EnemyPattern enemyPattern = pattern.get();
        assertThat(enemyPattern.loot()).isNotEmpty();
        assertThat(enemyPattern.loot().length).isPositive();
    }
    
    @Test
    public void givenThreshold_whenGetBossPatterns_thenReturnAllBossPatterns() {
        EnemyPattern[] enemyPatterns = settingsImporter.importBossPatterns();
        assertThat(enemyPatterns.length).isEqualTo(1);
    }
    
    @Test
    public void givenJson_whenImportRecipes_thenReturnRecipes() {
        List<Recipe> recipes = settingsImporter.importRecipes();
        
        assertThat(recipes).isNotNull();
        assertThat(recipes.size()).isPositive();
        
        Optional<Recipe> optionalRecipe = recipes.stream().findFirst();
        assertThat(optionalRecipe).isPresent();
        
        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getResultID()).isNotBlank();
        assertThat(recipe.getResultType()).isNotNull();
        assertThat(recipe.getResultAmount()).isPositive();
        assertThat(recipe.getMaterials()).isNotEmpty();
    }
}
