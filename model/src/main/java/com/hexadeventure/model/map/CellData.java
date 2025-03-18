package com.hexadeventure.model.map;

import lombok.Getter;

@Getter
public abstract class CellData {
    private final Vector2 position;
    protected CellType type;
    
    protected CellData(Vector2 position) {
        this.position = position;
    }
}
