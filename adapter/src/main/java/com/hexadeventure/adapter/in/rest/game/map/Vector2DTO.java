package com.hexadeventure.adapter.in.rest.game.map;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import lombok.NonNull;

import java.util.Objects;

public record Vector2DTO(int x, int y) {
    public static Vector2DTO fromModel(Vector2 vector2) {
        return new Vector2DTO(vector2.x, vector2.y);
    }
    
    public static Vector2DTO fromModel(Vector2C vector2C) {
        return new Vector2DTO(vector2C.x, vector2C.y);
    }
    
    public static Vector2 toModel(Vector2DTO position) {
        return new Vector2(position.x, position.y);
    }
    
    public static Vector2C toChunkModel(Vector2DTO position) {
        return new Vector2C(position.x, position.y);
    }
    
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
    @NonNull
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
