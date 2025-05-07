package com.hexadeventure.adapter.out.settings.json.recipe;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
public class RecipeJson {
    private String resultID;
    private ItemType resultType;
    private int resultAmount;
    private RecipeResourceJson[] materials;
    
    public Recipe toModel() {
        return new Recipe(resultID,
                          resultType,
                          resultAmount,
                          Arrays.stream(materials)
                                .map(RecipeResourceJson::toModel)
                                .toArray(RecipeResource[]::new));
    }
}
