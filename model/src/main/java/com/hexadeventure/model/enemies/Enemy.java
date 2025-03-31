package com.hexadeventure.model.enemies;

import com.hexadeventure.model.map.Vector2;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enemy {
    public static final int MOVEMENT_SPEED = 2;
    
    private Vector2 position;
    
    public Enemy(Vector2 position, double distanceToCenter) {
        this.position = position;
        // TODO
    }
}
