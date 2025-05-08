package com.hexadeventure.adapter.out.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.adapter.out.settings.json.enemy.EnemyJson;
import com.hexadeventure.adapter.out.settings.json.enemy.EnemyPatternJson;
import com.hexadeventure.adapter.out.settings.json.enemy.LootJson;
import com.hexadeventure.adapter.out.settings.json.food.FoodJson;
import com.hexadeventure.adapter.out.settings.json.initial.InitialResourcesJson;
import com.hexadeventure.adapter.out.settings.json.material.MaterialJson;
import com.hexadeventure.adapter.out.settings.json.potion.PotionJson;
import com.hexadeventure.adapter.out.settings.json.recipe.RecipeJson;
import com.hexadeventure.adapter.out.settings.json.weapon.WeaponDataJson;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.characters.EnemySetting;
import com.hexadeventure.model.inventory.characters.Loot;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

@Component
public class SettingsImporterAdapter implements SettingsImporter {
    private static final String WEAPONS_JSON = "weapons.json";
    private static final String FOODS_JSON = "foods.json";
    private static final String POTIONS_JSON = "potions.json";
    private static final String MATERIALS_JSON = "materials.json";
    
    private static final String INITIAL_RESOURCES_JSON = "initial_resources.json";
    
    private static final String ENEMIES_JSON = "enemies.json";
    private static final String ENEMY_PATTERNS_JSON = "enemy_patterns.json";
    
    private static final String RECIPES_JSON = "recipes.json";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Map<String, WeaponSetting> weaponsCache = new HashMap<>();
    private static final Set<WeaponSetting> weaponsSortedCache = new TreeSet<>(WeaponSetting::compareTo);
    private static final Map<String, Food> foodsCache = new HashMap<>();
    private static final Map<String, Potion> potionsCache = new HashMap<>();
    private static final Map<ResourceType, Material> materialsCache = new HashMap<>();
    private static final InitialResources initialResources = new InitialResources();
    private static final Map<String, EnemySetting> enemiesCache = new HashMap<>();
    private static final Set<EnemyPattern> enemyPatternsCache = new TreeSet<>();
    private static final List<Recipe> recipesCache = new ArrayList<>();
    
    @Override
    public Map<String, WeaponSetting> importWeapons() {
        return importJson(weaponsCache, WEAPONS_JSON, WeaponDataJson::getID, WeaponDataJson.class);
    }
    
    @Override
    public Map<WeaponType, List<WeaponSetting>> importWeaponsByTypeAndThreshold(double threshold) {
        Set<WeaponSetting> weapons = importWeaponsByThreshold(threshold);
        Map<WeaponType, List<WeaponSetting>> weponsToReturn = new HashMap<>();
        for (WeaponSetting weapon : weapons) {
            if(!weponsToReturn.containsKey(weapon.weaponType())) {
                weponsToReturn.put(weapon.weaponType(), new ArrayList<>());
            }
            weponsToReturn.get(weapon.weaponType()).add(weapon);
        }
        return weponsToReturn;
    }
    
    private Set<WeaponSetting> importWeaponsByThreshold(double threshold) {
        Set<WeaponSetting> weaponsSortedCache = importWeaponsSorted();
        Set<WeaponSetting> weaponsToReturn = new HashSet<>();
        for (WeaponSetting weaponSetting : weaponsSortedCache) {
            if(threshold >= weaponSetting.minThreshold() && threshold <= weaponSetting.maxThreshold()) {
                weaponsToReturn.add(weaponSetting);
            } else if(weaponSetting.minThreshold() > threshold) {
                break;
            }
        }
        return weaponsToReturn;
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
    
    @Override
    public InitialResources importInitialResources() {
        if(initialResources.getInitialWeapons() != null) return initialResources;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + INITIAL_RESOURCES_JSON);
            InputStream inputStream = new FileInputStream(file);
            
            InitialResourcesJson json = objectMapper.readValue(inputStream, InitialResourcesJson.class);
            InitialResources model = json.toModel();
            initialResources.setInitialCharacters(model.getInitialCharacters());
            initialResources.setInitialWeapons(model.getInitialWeapons());
            initialResources.setInitialFoods(model.getInitialFoods());
            initialResources.setInitialPotions(model.getInitialPotions());
            initialResources.setInitialMaterials(model.getInitialMaterials());
            return initialResources;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public EnemyPattern[] importEnemyPatterns(double threshold) {
        Set<EnemyPattern> enemyPatterns = importEnemyPatterns();
        Set<EnemyPattern> enemyPatternsToReturn = new HashSet<>();
        for (EnemyPattern enemyPattern : enemyPatterns) {
            if(enemyPattern.minThreshold() >= 0 && enemyPattern.minThreshold() <= threshold) {
                enemyPatternsToReturn.add(enemyPattern);
            } else if(enemyPattern.minThreshold() > threshold) {
                break;
            }
        }
        return enemyPatternsToReturn.toArray(EnemyPattern[]::new);
    }
    
    @Override
    public EnemyPattern[] importBossPatterns() {
        Set<EnemyPattern> enemyPatterns = importEnemyPatterns();
        Set<EnemyPattern> enemyPatternsToReturn = new HashSet<>();
        for (EnemyPattern enemyPattern : enemyPatterns) {
            if(enemyPattern.minThreshold() < 0) {
                enemyPatternsToReturn.add(enemyPattern);
            } else {
                break;
            }
        }
        return enemyPatternsToReturn.toArray(EnemyPattern[]::new);
    }
    
    @Override
    public List<Recipe> importRecipes() {
        if(!recipesCache.isEmpty()) return recipesCache;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + RECIPES_JSON);
            InputStream inputStream = new FileInputStream(file);
            
            RecipeJson[] json = objectMapper.readValue(inputStream, RecipeJson[].class);
            List<Recipe> model = Arrays.stream(json).map(RecipeJson::toModel)
                                       .toList();
            recipesCache.addAll(model);
            return recipesCache;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("SameReturnValue")
    private Map<String, EnemySetting> importEnemies() {
        if(!enemiesCache.isEmpty()) return enemiesCache;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + ENEMIES_JSON);
            InputStream inputStream = new FileInputStream(file);
            
            EnemyJson[] json = objectMapper.readValue(inputStream, EnemyJson[].class);
            for (EnemyJson enemy : json) {
                enemiesCache.put(enemy.getId(), enemy.toModel());
            }
            return enemiesCache;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("SameReturnValue")
    private Set<EnemyPattern> importEnemyPatterns() {
        if(!enemyPatternsCache.isEmpty()) return enemyPatternsCache;
        try {
            // From: https://stackoverflow.com/a/49468282/11451105
            File file = ResourceUtils.getFile("classpath:" + ENEMY_PATTERNS_JSON);
            InputStream inputStream = new FileInputStream(file);
            
            EnemyPatternJson[] json = objectMapper.readValue(inputStream, EnemyPatternJson[].class);
            Map<String, EnemySetting> enemies = importEnemies();
            for (EnemyPatternJson pattern : json) {
                EnemySetting[][] enemiesArray = pattern.toModel(enemies,
                                                                GameMap.COMBAT_TERRAIN_ROW_SIZE,
                                                                GameMap.COMBAT_TERRAIN_COLUMN_SIZE);
                Loot[] loot = Arrays.stream(pattern.getLoot()).map(LootJson::toModel).toArray(Loot[]::new);
                enemyPatternsCache.add(new EnemyPattern(pattern.getMinThreshold(), enemiesArray, loot));
            }
            return enemyPatternsCache;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("SameReturnValue")
    private Set<WeaponSetting> importWeaponsSorted() {
        if(!weaponsSortedCache.isEmpty()) return weaponsSortedCache;
        Map<String, WeaponSetting> weapons = importWeapons();
        weaponsSortedCache.addAll(weapons.values());
        return weaponsSortedCache;
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
            return cache;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
