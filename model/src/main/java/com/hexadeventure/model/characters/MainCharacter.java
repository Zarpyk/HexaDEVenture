package com.hexadeventure.model.characters;

import lombok.Getter;
import lombok.Setter;
import com.hexadeventure.model.map.Vector2;

@Getter
@Setter
public class MainCharacter {
    private Vector2 position;
    
    public MainCharacter(Vector2 position) {
        this.position = position;
    }
}
