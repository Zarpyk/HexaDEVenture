package com.hexadeventure.model.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class CellData {
    private Vector2 position;
    protected CellType type;
    
    protected CellData(Vector2 position) {
        this.position = position;
    }
}
