package com.hexadeventure.adapter.out.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.adapter.out.settings.json.food.FoodJson;
import com.hexadeventure.adapter.out.settings.json.material.MaterialJson;
import com.hexadeventure.adapter.out.settings.json.potion.PotionJson;
import com.hexadeventure.adapter.out.settings.json.weapon.WeaponDataJson;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.WeaponData;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Component
public class SettingsImporterAdapter implements SettingsImporter {
    private static final String WEAPONS_JSON = "weapons.json";
    private static final String FOODS_JSON = "foods.json";
    private static final String POTIONS_JSON = "potions.json";
    private static final String MATERIALS_JSON = "materials.json";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Set<WeaponData> weaponsCache = new HashSet<>();
    private static final Set<Food> foodsCache = new HashSet<>();
    private static final Set<Potion> potionsCache = new HashSet<>();
    private static final Set<Material> materialsCache = new HashSet<>();
    
    @Override
    public Set<WeaponData> importWeapons() {
        return importJson(weaponsCache, WEAPONS_JSON, WeaponDataJson.class);
    }
    
    @Override
    public Set<Food> importFoods() {
        return importJson(foodsCache, FOODS_JSON, FoodJson.class);
    }
    
    @Override
    public Set<Potion> importPotions() {
        return importJson(potionsCache, POTIONS_JSON, PotionJson.class);
    }
    
    @Override
    public Set<Material> importMaterials() {
        return importJson(materialsCache, MATERIALS_JSON, MaterialJson.class);
    }
    
    private <T, T2 extends ItemJson<T>> Set<T> importJson(Set<T> cache, String jsonFile, Class<T2> clazz) {
        if(!cache.isEmpty()) return cache;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + jsonFile);
            InputStream inputStream = new FileInputStream(file);
            
            T2[] foods = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructArrayType(clazz));
            for (T2 food : foods) {
                cache.add(food.toModel());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cache;
    }
}
