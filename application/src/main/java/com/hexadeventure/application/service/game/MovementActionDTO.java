package com.hexadeventure.application.service.game;

import java.util.List;

public record MovementActionDTO(int x, int y, ResourceDTO resource, List<EnemyMovementDTO> enemyMovements) {
}
