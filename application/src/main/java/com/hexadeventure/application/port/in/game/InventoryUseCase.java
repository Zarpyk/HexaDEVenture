package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.inventory.recipes.Recipe;

public interface InventoryUseCase {
    Recipe[] getRecipes(String email, int page, int size);
    void craft(String email, int recipeIndex, int count);
}
