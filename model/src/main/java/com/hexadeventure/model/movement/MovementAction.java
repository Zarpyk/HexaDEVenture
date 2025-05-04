package com.hexadeventure.model.movement;

import com.hexadeventure.model.map.Vector2;

import java.util.List;

public record MovementAction(Vector2 position, ResourceAction resourceAction, List<EnemyMovement> enemyMovements) {
}
