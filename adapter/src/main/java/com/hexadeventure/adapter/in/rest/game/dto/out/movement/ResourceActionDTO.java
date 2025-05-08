package com.hexadeventure.adapter.in.rest.game.dto.out.movement;

import com.hexadeventure.model.movement.ResourceAction;

public record ResourceActionDTO(int type, int amount) {
    public static ResourceActionDTO fromModel(ResourceAction resourceAction) {
        return new ResourceActionDTO(resourceAction.type(), resourceAction.amount());
    }
}
