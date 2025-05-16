package com.hexadeventure.model.movement;

import com.hexadeventure.model.map.Vector2;

public record EnemyMovement(Vector2 originalPosition, Vector2 targetPosition) {
}
