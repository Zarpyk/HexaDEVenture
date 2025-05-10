package com.hexadeventure.model.map;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MainCharacter {
    private Vector2 position;
    
    public MainCharacter(Vector2 position) {
        this.position = position;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MainCharacter that)) return false;
        return Objects.equals(position, that.position);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(position);
    }
}
