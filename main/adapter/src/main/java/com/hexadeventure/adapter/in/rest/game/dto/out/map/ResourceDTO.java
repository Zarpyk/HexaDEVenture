package com.hexadeventure.adapter.in.rest.game.dto.out.map;

import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.map.resources.ResourceType;

public record ResourceDTO(Vector2DTO position,
                          ResourceType type,
                          int count) {
    public static ResourceDTO fromModel(Resource model) {
        return new ResourceDTO(Vector2DTO.fromModel(model.getPosition()),
                               model.getType(),
                               model.getCount());
    }
}
