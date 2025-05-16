package com.hexadeventure.adapter.in.rest.game.dto.out.movement;

import com.hexadeventure.model.movement.MovementResponse;

import java.util.List;

public record MovementResponseDTO(List<MovementActionDTO> actions) {
    public static MovementResponseDTO fromModel(MovementResponse model) {
        return new MovementResponseDTO(model.actions().stream().map(MovementActionDTO::fromModel).toList());
    }
}

