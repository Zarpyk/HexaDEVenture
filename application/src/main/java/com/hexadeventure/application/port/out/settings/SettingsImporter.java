package com.hexadeventure.application.port.out.settings;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.List;
import java.util.Map;

public interface SettingsImporter {
    Map<String, WeaponSetting> importWeapons();
    Map<WeaponType, List<WeaponSetting>> importWeaponsByTypeAndThreshold(double threshold);
    Map<String, Food> importFoods();
    Map<String, Potion> importPotions();
    Map<ResourceType, Material> importMaterials();
    InitialResources importInitialResources();
    EnemyPattern[] importEnemyPatterns(double threshold);
    EnemyPattern[] importBossPatterns();
    List<Recipe> importRecipes();
}
