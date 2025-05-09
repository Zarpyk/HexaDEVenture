package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.recipes.Recipe;

public interface InventoryUseCase {
    Recipe[] getRecipes(String email, int page, int size);
    void craft(String email, int recipeIndex, int count);
    Inventory getInventory(String email);
    void equipWeapon(String email, String characterId, String weaponId);
    void unequipWeapon(String email, String characterId);
    void useItem(String email, String characterId, String itemId);
}
