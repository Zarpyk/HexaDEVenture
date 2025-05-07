package com.hexadeventure.adapter.out.settings.json.recipe;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeResourceJson {
    private String id;
    private ItemType type;
    private int amount;
    
    public RecipeResource toModel() {
        return new RecipeResource(id, type, amount);
    }
}
