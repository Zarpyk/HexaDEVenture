package com.hexadeventure.adapter.in.rest.game.movement;

import com.hexadeventure.model.movement.ResourceAction;

public record ResourceActionDTO(String type, int amount) {
    public static ResourceActionDTO fromModel(ResourceAction resourceAction) {
        return new ResourceActionDTO(resourceAction.type(), resourceAction.amount());
    }
}
