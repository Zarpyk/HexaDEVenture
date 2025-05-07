package com.hexadeventure.model.inventory.recipes;

import com.hexadeventure.model.inventory.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Recipe {
    private String resultID;
    private ItemType resultType;
    private int resultAmount;
    private RecipeResource[] materials;
}
