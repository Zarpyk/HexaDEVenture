package com.hexadeventure.model.enemies;

import com.hexadeventure.model.map.Vector2;
import lombok.Getter;

@Getter
public class Enemy {
    private Vector2 position;
    
    public Enemy(Vector2 position, double distanceToCenter) {
        this.position = position;
        // TODO
    }
}
