package com.hexadeventure.model.inventory.recipes;

import com.hexadeventure.model.inventory.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeResource {
    private String id;
    private ItemType type;
    private int count;
}
