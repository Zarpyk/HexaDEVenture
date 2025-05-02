package com.hexadeventure.adapter.out.settings;

import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.map.resources.ResourceType;
import org.junit.Test;

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
        
        assertThat(weaponsData.size()).isEqualTo(2);
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
        assertThat(weaponSetting.minHipnotizationPower()).isPositive();
        assertThat(weaponSetting.maxHipnotizationPower()).isPositive();
    }
    
    @Test
    public void givenJson_whenImportFoods_thenReturnFoodsList() {
        Map<String, Food> foods = settingsImporter.importFoods();
        
        assertThat(foods.size()).isEqualTo(2);
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
        
        assertThat(potions.size()).isEqualTo(2);
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
        
        assertThat(materials.size()).isEqualTo(2);
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
        assertThat(initialResources.getInitialWeapons().length).isPositive();
        assertThat(initialResources.getInitialFoods().length).isPositive();
        assertThat(initialResources.getInitialPotions().length).isPositive();
        assertThat(initialResources.getInitialMaterials().length).isPositive();
    }
}
