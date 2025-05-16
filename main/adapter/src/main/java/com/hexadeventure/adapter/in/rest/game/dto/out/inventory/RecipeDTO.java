package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.recipes.Recipe;

import java.util.Arrays;

public record RecipeDTO(String resultId,
                        ItemType resultType,
                        int resultCount,
                        RecipeResourceDTO[] materials,
                        int craftableCount) {
    
    public static RecipeDTO fromModel(Recipe recipe) {
        if (recipe == null) return null;
        return new RecipeDTO(recipe.getResultId(),
                             recipe.getResultType(),
                             recipe.getResultAmount(),
                             Arrays.stream(recipe.getMaterials())
                                   .map(RecipeResourceDTO::fromModel)
                                   .toArray(RecipeResourceDTO[]::new),
                             recipe.getCraftableAmount());
    }
}
