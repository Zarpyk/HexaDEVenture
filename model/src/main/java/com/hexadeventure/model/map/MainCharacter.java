package com.hexadeventure.model.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainCharacter {
    private Vector2 position;
    
    public MainCharacter(Vector2 position) {
        this.position = position;
    }
}
