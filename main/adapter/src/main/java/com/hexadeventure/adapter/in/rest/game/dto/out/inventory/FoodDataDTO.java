package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.foods.Food;

public record FoodDataDTO(String id,
                          String name,
                          ItemType itemType,
                          int skin,
                          int count,
                          double healthPoints) {
    public static FoodDataDTO fromModel(Food model) {
        return new FoodDataDTO(model.getId(),
                               model.getName(),
                               model.getType(),
                               model.getSkin(),
                               model.getCount(),
                               model.getHealthPoints());
    }
}
