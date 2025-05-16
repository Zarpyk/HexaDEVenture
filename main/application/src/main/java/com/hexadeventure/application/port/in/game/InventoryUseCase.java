package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.Weapon;

public interface InventoryUseCase {
    int getRecipesCount(String email);
    Recipe[] getRecipes(String email, int page, int size);
    void craft(String email, int recipeIndex, int count);
    Inventory getInventory(String email);
    PlayableCharacter getCharacter(String email, String characterId);
    Weapon getWeapon(String email, String weaponId);
    Potion getPotion(String email, String potionId);
    Food getFood(String email, String foodId);
    Material getMaterial(String email, String materialId);
    void equipWeapon(String email, String characterId, String weaponId);
    void unequipWeapon(String email, String characterId);
    void useItem(String email, String characterId, String itemId);
}
