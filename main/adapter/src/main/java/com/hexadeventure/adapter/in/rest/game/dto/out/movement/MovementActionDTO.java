package com.hexadeventure.adapter.in.rest.game.dto.out.movement;

import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.model.movement.MovementAction;

import java.util.List;

public record MovementActionDTO(Vector2DTO originalPosition, Vector2DTO targetPosition,
                                ResourceActionDTO resource, List<EnemyMovementDTO> enemyMovements) {
    public static MovementActionDTO fromModel(MovementAction model) {
        return new MovementActionDTO(Vector2DTO.fromModel(model.originalPosition()),
                                     Vector2DTO.fromModel(model.targetPosition()),
                                     ResourceActionDTO.fromModel(model.resourceAction()),
                                     model.enemyMovements().stream().map(EnemyMovementDTO::fromModel).toList());
    }
}
