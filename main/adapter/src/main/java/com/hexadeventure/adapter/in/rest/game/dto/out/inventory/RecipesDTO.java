package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.recipes.Recipe;

import java.util.Arrays;

public record RecipesDTO(RecipeDTO[] recipes) {
    public static RecipesDTO fromModel(Recipe[] recipes) {
        return new RecipesDTO(Arrays.stream(recipes)
                                    .map(RecipeDTO::fromModel)
                                    .toArray(RecipeDTO[]::new));
    }
}
