package com.hexadeventure.model.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CellData {
    private Vector2 position;
    private CellType type;
    
    public CellData(Vector2 position, CellType type) {
        this.position = position;
        this.type = type;
    }
}
