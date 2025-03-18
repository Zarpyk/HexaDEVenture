package com.hexadeventure.model.map;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Vector2 {
    public int x;
    public int y;
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Vector2 vector2)) return false;
        return x == vector2.x && y == vector2.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return '(' + x + ", " + y + ')';
    }
}
