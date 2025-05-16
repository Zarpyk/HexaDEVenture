package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;

public record ItemDTO(String id,
                      String name,
                      ItemType itemType,
                      int skin,
                      int count) {
    
    public static ItemDTO fromModel(Item item) {
        return new ItemDTO(item.getId(),
                           item.getName(),
                           item.getType(),
                           item.getSkin(),
                           item.getCount());
    }
}
