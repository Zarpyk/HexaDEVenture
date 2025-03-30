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
import com.hexadeventure.model.map.resources.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class SettingsImporterAdapter implements SettingsImporter {
    private static final String WEAPONS_JSON = "weapons.json";
    private static final String FOODS_JSON = "foods.json";
    private static final String POTIONS_JSON = "potions.json";
    private static final String MATERIALS_JSON = "materials.json";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Map<String, WeaponData> weaponsCache = new HashMap<>();
    private static final Map<String, Food> foodsCache = new HashMap<>();
    private static final Map<String, Potion> potionsCache = new HashMap<>();
    private static final Map<ResourceType, Material> materialsCache = new HashMap<>();
    
    @Override
    public Map<String, WeaponData> importWeapons() {
        return importJson(weaponsCache, WEAPONS_JSON, WeaponDataJson::getID, WeaponDataJson.class);
    }
    
    @Override
    public Map<String, Food> importFoods() {
        return importJson(foodsCache, FOODS_JSON, FoodJson::getID, FoodJson.class);
    }
    
    @Override
    public Map<String, Potion> importPotions() {
        return importJson(potionsCache, POTIONS_JSON, PotionJson::getID, PotionJson.class);
    }
    
    @Override
    public Map<ResourceType, Material> importMaterials() {
        return importJson(materialsCache, MATERIALS_JSON, MaterialJson::getID, MaterialJson.class);
    }
    
    private <T, ID, JSON_T extends ItemJson<T>> Map<ID, T> importJson(Map<ID, T> cache, String jsonFile,
                                                                      Function<JSON_T, ID> idExtractor,
                                                                      Class<JSON_T> clazz) {
        if(!cache.isEmpty()) return cache;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + jsonFile);
            InputStream inputStream = new FileInputStream(file);
            
            JSON_T[] json = objectMapper.readValue(inputStream,
                                                   objectMapper.getTypeFactory().constructArrayType(clazz));
            for (JSON_T jsonObject : json) {
                cache.put(idExtractor.apply(jsonObject), jsonObject.toModel());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cache;
    }
}
