package com.hexadeventure.adapter.in.rest.game.dto.out.movement;

import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.model.movement.EnemyMovement;

public record EnemyMovementDTO(Vector2DTO position) {
    public static EnemyMovementDTO fromModel(EnemyMovement enemyMovement) {
        return new EnemyMovementDTO(Vector2DTO.fromModel(enemyMovement.position()));
    }
}
