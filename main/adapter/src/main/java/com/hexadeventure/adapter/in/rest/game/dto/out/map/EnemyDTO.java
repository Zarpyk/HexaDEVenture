package com.hexadeventure.adapter.in.rest.game.dto.out.map;

import com.hexadeventure.model.enemies.Enemy;

public record EnemyDTO(Vector2DTO position) {
    public static EnemyDTO fromModel(Enemy model) {
        return new EnemyDTO(Vector2DTO.fromModel(model.getPosition()));
    }
}
