package com.hexadeventure.model.enemies;

import com.hexadeventure.model.map.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Enemy {
    private Vector2 position;
    
    public Enemy(Vector2 position) {
        this.position = position;
    }
}
