package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.map.resources.ResourceType;

public record MaterialDataDTO(String id,
                              String name,
                              ItemType itemType,
                              int skin,
                              int count,
                              ResourceType materialType) {
    public static MaterialDataDTO fromModel(Material model) {
        return new MaterialDataDTO(model.getId(),
                                   model.getName(),
                                   model.getType(),
                                   model.getSkin(),
                                   model.getCount(),
                                   model.getMaterialType());
    }
}
