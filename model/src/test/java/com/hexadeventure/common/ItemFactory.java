package com.hexadeventure.common;

import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;
import com.hexadeventure.model.map.resources.ResourceType;

public class ItemFactory {
    public static final String TEST_FOOD_NAME = "TestFood";
    public static final String TEST_POTION_NAME = "TestPotion";
    public static final String TEST_MATERIAL_NAME = "TestMaterial";
    
    public static Food createFood() {
        return new Food(TEST_FOOD_NAME, 1);
    }
    
    public static Potion createPotion() {
        return new Potion(TEST_POTION_NAME, 1, 1, PotionType.HEALING);
    }
    
    public static Material createMaterial() {
        return new Material(TEST_MATERIAL_NAME, 1, ResourceType.WOOD);
    }
}
