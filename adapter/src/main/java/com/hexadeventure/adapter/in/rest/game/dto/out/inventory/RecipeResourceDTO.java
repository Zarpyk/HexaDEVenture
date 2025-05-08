package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.recipes.RecipeResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeResourceDTO {
    private String id;
    private ItemType type;
    private int count;
    
    public static RecipeResourceDTO fromModel(RecipeResource model) {
        return new RecipeResourceDTO(model.getId(), model.getType(), model.getCount());
    }
}
