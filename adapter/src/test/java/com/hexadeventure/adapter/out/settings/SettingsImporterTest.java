package com.hexadeventure.adapter.out.settings;

import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.WeaponData;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingsImporterTest {
    private final SettingsImporterAdapter settingsImporter;
    
    public SettingsImporterTest() {
        settingsImporter = new SettingsImporterAdapter();
    }
    
    @Test
    public void givenJson_whenImportWeapons_thenReturnWeaponDataList() {
        Set<WeaponData> weaponsData = settingsImporter.importWeapons();
        
        assertThat(weaponsData.size()).isEqualTo(2);
        Optional<WeaponData> weapon = weaponsData.stream().findFirst();
        assertThat(weapon).isPresent();
        
        WeaponData weaponData = weapon.get();
        assertThat(weaponData.name()).isNotBlank();
        assertThat(weaponData.skin()).isPositive();
        assertThat(weaponData.weaponType()).isNotNull();
        assertThat(weaponData.minDamage()).isPositive();
        assertThat(weaponData.maxDamage()).isPositive();
        assertThat(weaponData.minMeleeDefense()).isPositive();
        assertThat(weaponData.maxMeleeDefense()).isPositive();
        assertThat(weaponData.minRangedDefense()).isPositive();
        assertThat(weaponData.maxRangedDefense()).isPositive();
        assertThat(weaponData.minCooldown()).isPositive();
        assertThat(weaponData.maxCooldown()).isPositive();
        assertThat(weaponData.aggroGenType()).isNotNull();
        assertThat(weaponData.extraAggroGeneration()).isPositive();
        assertThat(weaponData.minAggroGeneration()).isPositive();
        assertThat(weaponData.maxAggroGeneration()).isPositive();
        assertThat(weaponData.initialAggro()).isPositive();
        assertThat(weaponData.healingPower()).isPositive();
        assertThat(weaponData.hipnotizationPower()).isPositive();
    }
    
    @Test
    public void givenJson_whenImportFoods_thenReturnFoodsList() {
        Set<Food> foods = settingsImporter.importFoods();
        
        assertThat(foods.size()).isEqualTo(2);
        Optional<Food> food = foods.stream().findFirst();
        assertThat(food).isPresent();
        
        Food foodObj = food.get();
        assertThat(foodObj.getName()).isNotBlank();
        assertThat(foodObj.getSkin()).isPositive();
        assertThat(foodObj.getHealthPoints()).isPositive();
    }
    
    @Test
    public void givenJson_whenImportPotions_thenReturnPotionsList() {
        Set<Potion> potions = settingsImporter.importPotions();
        
        assertThat(potions.size()).isEqualTo(2);
        Optional<Potion> potion = potions.stream().findFirst();
        assertThat(potion).isPresent();
        
        Potion potionObj = potion.get();
        assertThat(potionObj.getName()).isNotBlank();
        assertThat(potionObj.getSkin()).isPositive();
        assertThat(potionObj.getPotionPower()).isPositive();
        assertThat(potionObj.getPotionType()).isNotNull();
    }
    
    @Test
    public void givenJson_whenImportMaterials_thenReturnMaterialsList() {
        Set<Material> materials = settingsImporter.importMaterials();
        
        assertThat(materials.size()).isEqualTo(2);
        Optional<Material> material = materials.stream().findFirst();
        assertThat(material).isPresent();
        
        Material materialObj = material.get();
        assertThat(materialObj.getName()).isNotBlank();
        assertThat(materialObj.getSkin()).isPositive();
        assertThat(materialObj.getMaterialType()).isNotNull();
    }
}
