package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.InvalidRecipeException;
import com.hexadeventure.application.exceptions.InvalidSearchException;
import com.hexadeventure.application.exceptions.NotEnoughtResourcesException;
import com.hexadeventure.application.exceptions.SizeException;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class InventoryService implements InventoryUseCase {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    private final SettingsImporter settingsImporter;
    
    public InventoryService(UserRepository userRepository, GameMapRepository gameMapRepository,
                            SettingsImporter settingsImporter) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.settingsImporter = settingsImporter;
    }
    
    @Override
    public Recipe[] getRecipes(String email, int page, int size) {
        List<Recipe> recipes = settingsImporter.importRecipes();
        int totalRecipes = recipes.size();
        
        if(page <= 0 || size <= 0 || page * size > totalRecipes) {
            throw new InvalidSearchException();
        }
        
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        Recipe[] result = new Recipe[size];
        Map<String, Item> items = gameMap.getInventory().getItems();
        for (int i = page * size - size; i < page * size; i++) {
            result[i % size] = recipes.get(i);
            int craftableAmount = getCraftableAmount(items, result[i % size].getMaterials());
            if(craftableAmount > 0) {
                result[i % size].setCraftableAmount(craftableAmount);
            }
        }
        return result;
    }
    
    @Override
    public void craft(String email, int recipeIndex, int count) {
        if(count <= 0) {
            throw new SizeException("Crafting count must be greater than 0");
        }
        
        List<Recipe> recipes = settingsImporter.importRecipes();
        if(recipeIndex < 0 || recipeIndex >= recipes.size()) {
            throw new InvalidRecipeException(recipeIndex);
        }
        
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        Inventory inventory = gameMap.getInventory();
        Map<String, Item> items = inventory.getItems();
        
        // Check if the recipe is craftable
        Recipe recipe = recipes.get(recipeIndex);
        int craftableAmount = getCraftableAmount(items, recipe.getMaterials());
        if(craftableAmount < count) {
            throw new NotEnoughtResourcesException();
        }
        
        // Remove the materials from the inventory
        for (RecipeResource material : recipe.getMaterials()) {
            Item item = items.get(material.getId());
            inventory.removeItem(item, material.getCount() * count);
        }
        
        craftRecipeAndAddToInventory(count, recipe, inventory);
    }
    
    private static int getCraftableAmount(Map<String, Item> inventoryItems,
                                          RecipeResource[] materials) {
        int minCraftableAmount = Integer.MAX_VALUE;
        for (RecipeResource material : materials) {
            Item item = inventoryItems.get(material.getId());
            if(item == null || item.getCount() < material.getCount()) {
                minCraftableAmount = -1;
                break;
            }
            int craftableAmount = item.getCount() / material.getCount();
            if(craftableAmount < minCraftableAmount) {
                minCraftableAmount = craftableAmount;
            }
        }
        return minCraftableAmount;
    }
    
    private void craftRecipeAndAddToInventory(int count, Recipe recipe, Inventory inventory) {
        switch (recipe.getResultType()) {
            case WEAPON -> {
                Map<String, WeaponSetting> weapons = settingsImporter.importWeapons();
                WeaponSetting setting = weapons.get(recipe.getResultID());
                SplittableRandom random = new SplittableRandom();
                for (int i = 0; i < recipe.getResultAmount() * count; i++) {
                    Weapon weapon = new Weapon(setting, random);
                    inventory.addItem(weapon);
                }
            }
            case FOOD -> {
                Map<String, Food> food = settingsImporter.importFoods();
                Item foodItem = food.get(recipe.getResultID());
                inventory.addItem(foodItem, recipe.getResultAmount() * count);
            }
            case POTION -> {
                Map<String, Potion> potions = settingsImporter.importPotions();
                Item potionItem = potions.get(recipe.getResultID());
                inventory.addItem(potionItem, recipe.getResultAmount() * count);
            }
            case MATERIAL -> {
                Map<ResourceType, Material> materials = settingsImporter.importMaterials();
                Item materialItem = materials.get(ResourceType.valueOf(recipe.getResultID()));
                inventory.addItem(materialItem, recipe.getResultAmount() * count);
            }
        }
    }
}
