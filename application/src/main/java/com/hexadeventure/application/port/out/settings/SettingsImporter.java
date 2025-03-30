package com.hexadeventure.application.port.out.settings;

import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.WeaponData;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.Map;

public interface SettingsImporter {
    Map<String, WeaponData> importWeapons();
    Map<String, Food> importFoods();
    Map<String, Potion> importPotions();
    Map<ResourceType, Material> importMaterials();
}
