package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
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
import java.util.Objects;
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
        
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
        
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
        
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
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
        
        gameMapRepository.save(gameMap);
    }
    
    @Override
    public Inventory getInventory(String email) {
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
        return gameMap.getInventory();
    }
    
    @Override
    public void equipWeapon(String email, String characterId, String weaponId) {
        useItem(email, characterId, weaponId);
    }
    
    @Override
    public void unequipWeapon(String email, String characterId) {
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
        Inventory inventory = gameMap.getInventory();
        
        if(characterId == null) throw new InvalidCharacterException();
        
        Map<String, PlayableCharacter> characters = inventory.getCharacters();
        PlayableCharacter character = characters.get(characterId);
        if(character == null || Objects.equals(character.getWeapon().getName(), Weapon.DEFAULT_WEAPON.getName()))
            throw new InvalidCharacterException();
        
        inventory.addItem(character.getWeapon());
        character.setWeapon(null);
        
        gameMapRepository.save(gameMap);
    }
    
    @Override
    public void useItem(String email, String characterId, String itemId) {
        GameMap gameMap = Utilities.getUserGameMap(email, userRepository, gameMapRepository);
        Inventory inventory = gameMap.getInventory();
        
        if(characterId == null) throw new InvalidCharacterException();
        if(itemId == null) throw new InvalidItemException();
        
        getCharacterAndUseItem(inventory, characterId, itemId);
        
        gameMapRepository.save(gameMap);
    }
    
    private void getCharacterAndUseItem(Inventory inventory, String characterId, String itemId) {
        Map<String, PlayableCharacter> characters = inventory.getCharacters();
        PlayableCharacter character = characters.get(characterId);
        if(character == null) throw new InvalidCharacterException();
        
        Map<String, Item> items = inventory.getItems();
        Item item = items.get(itemId);
        if(item == null) throw new InvalidItemException();
        useItem(character, item);
        inventory.removeItem(item, 1);
    }
    
    private void useItem(PlayableCharacter character, Item item) {
        switch (item.getType()) {
            case WEAPON -> {
                if(!(item instanceof Weapon weapon)) throw new InvalidItemException();
                character.setWeapon(weapon);
            }
            case FOOD -> {
                if(!(item instanceof Food food)) throw new InvalidItemException();
                character.getChangedStats().heal(character.getHealth(), food.getHealthPoints());
            }
            case POTION -> {
                if(!(item instanceof Potion potion)) throw new InvalidItemException();
                switch (potion.getPotionType()) {
                    case HEALING -> character.getChangedStats().setBoostHealth(potion.getPotionPower());
                    case SPEED -> character.getChangedStats().setBoostSpeed(potion.getPotionPower());
                    case STRENGTH -> character.getChangedStats().setBoostStrength(potion.getPotionPower());
                    case DEFENSE -> character.getChangedStats().setBoostDefense(potion.getPotionPower());
                    default -> throw new IllegalStateException("Unexpected value: " + potion.getPotionType());
                }
            }
            default -> throw new InvalidItemException();
        }
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
