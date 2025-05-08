package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.InvalidSearchException;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import com.hexadeventure.model.map.GameMap;

import java.util.List;
import java.util.Map;

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
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        List<Recipe> recipes = settingsImporter.importRecipes();
        int totalRecipes = recipes.size();
        
        if(page <= 0 || size <= 0 || page * size > totalRecipes) {
            throw new InvalidSearchException();
        }
        
        Recipe[] result = new Recipe[size];
        Map<String, Item> items = gameMap.getInventory().getItems();
        for (int i = page * size - size; i < page * size; i++) {
            result[i % size] = recipes.get(i);
            boolean hasAllMaterials = true;
            int minCraftableAmount = Integer.MAX_VALUE;
            for (RecipeResource material : result[i % size].getMaterials()) {
                Item item = items.get(material.getId());
                if(item == null || item.getCount() < material.getCount()) {
                    hasAllMaterials = false;
                    break;
                }
                int craftableAmount = item.getCount() / material.getCount();
                if(craftableAmount < minCraftableAmount) {
                    minCraftableAmount = craftableAmount;
                }
            }
            if(hasAllMaterials) {
                result[i % size].setCraftableAmount(minCraftableAmount);
            }
        }
        return result;
    }
}
