package com.hexadeventure.adapter.in.rest.game.movement;

import com.hexadeventure.model.movement.EnemyMovement;

public record EnemyMovementDTO(int x, int y) {
    public static EnemyMovementDTO fromModel(EnemyMovement enemyMovement) {
        return new EnemyMovementDTO(enemyMovement.x(), enemyMovement.y());
    }
}
