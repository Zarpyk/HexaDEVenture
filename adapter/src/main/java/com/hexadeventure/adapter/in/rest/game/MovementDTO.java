package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.model.map.Vector2;

public record MovementDTO(int x, int y) {
    public Vector2 toModel() {
        return new Vector2(x, y);
    }
}
