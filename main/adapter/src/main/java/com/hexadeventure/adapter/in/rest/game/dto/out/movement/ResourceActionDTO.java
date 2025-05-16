package com.hexadeventure.adapter.in.rest.game.dto.out.movement;

import com.hexadeventure.model.map.resources.ResourceType;
import com.hexadeventure.model.movement.ResourceAction;

public record ResourceActionDTO(ResourceType type, int amount) {
    public static ResourceActionDTO fromModel(ResourceAction resourceAction) {
        if(resourceAction == null) return null;
        return new ResourceActionDTO(resourceAction.type(), resourceAction.amount());
    }
}
