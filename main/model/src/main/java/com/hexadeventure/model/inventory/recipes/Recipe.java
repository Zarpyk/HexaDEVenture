package com.hexadeventure.model.inventory.recipes;

import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Recipe {
    private final String resultId;
    private final ItemType resultType;
    private final int resultAmount;
    private final RecipeResource[] materials;
    
    @Setter
    private int craftableAmount;
    
    public Recipe(String resultId, ItemType resultType, int resultAmount, RecipeResource[] materials) {
        this.resultId = resultId;
        this.resultType = resultType;
        this.resultAmount = resultAmount;
        this.materials = materials;
    }
}
