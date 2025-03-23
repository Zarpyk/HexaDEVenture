package com.hexadeventure.adapter.in.rest.game.movement;

import com.hexadeventure.model.movement.MovementAction;

import java.util.List;

public record MovementActionDTO(int x, int y, ResourceActionDTO resource, List<EnemyMovementDTO> enemyMovements) {
    public static MovementActionDTO fromModel(MovementAction model) {
        return new MovementActionDTO(model.x(),
                                     model.y(),
                                     ResourceActionDTO.fromModel(model.resourceAction()),
                                     model.enemyMovements().stream().map(EnemyMovementDTO::fromModel).toList());
    }
}
