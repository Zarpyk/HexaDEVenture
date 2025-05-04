package com.hexadeventure.adapter.in.rest.game.movement;

import com.hexadeventure.adapter.in.rest.game.map.Vector2DTO;
import com.hexadeventure.model.movement.EnemyMovement;

public record EnemyMovementDTO(Vector2DTO position) {
    public static EnemyMovementDTO fromModel(EnemyMovement enemyMovement) {
        return new EnemyMovementDTO(Vector2DTO.fromModel(enemyMovement.position()));
    }
}
