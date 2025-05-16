package com.hexadeventure.model.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CellData {
    public static final float EMPTY_THRESHOLD = -0.07f;
    public static final float EMPTY2_THRESHOLD = 0.07f;
    
    private Vector2 position;
    private CellType type;
    
    public CellData(Vector2 position, double threshold) {
        this.position = position;
        if(threshold < EMPTY_THRESHOLD) {
            type = CellType.GROUND;
        } else if(threshold < EMPTY2_THRESHOLD) {
            type = CellType.GROUND2;
        } else {
            type = CellType.WALL;
        }
    }
    
    public CellData(Vector2 position, CellType type) {
        this.position = position;
        this.type = type;
    }
}
