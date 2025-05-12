package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.recipes.RecipeResource;

public record RecipeResourceDTO(String id,
                                ItemType type,
                                int count) {
    public static RecipeResourceDTO fromModel(RecipeResource model) {
        return new RecipeResourceDTO(model.getId(), model.getType(), model.getCount());
    }
}
