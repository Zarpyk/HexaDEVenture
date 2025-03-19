package com.hexadeventure.model.map;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyCell extends CellData {
    public EmptyCell(Vector2 position) {
        super(position);
        type = CellType.EMPTY;
    }
}
