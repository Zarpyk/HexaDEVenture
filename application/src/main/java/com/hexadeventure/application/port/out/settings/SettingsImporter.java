package com.hexadeventure.application.port.out.settings;

import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.WeaponData;

import java.util.Set;

public interface SettingsImporter {
    Set<WeaponData> importWeapons();
    Set<Food> importFoods();
    Set<Potion> importPotions();
    Set<Material> importMaterials();
}
