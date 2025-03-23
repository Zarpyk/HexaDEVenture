package com.hexadeventure.model.movement;

import java.util.List;

public record MovementAction(int x, int y, ResourceAction resourceAction, List<EnemyMovement> enemyMovements) {
}
