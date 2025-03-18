package com.hexadeventure.model.map;

public class EmptyCell extends CellData {
    public EmptyCell(Vector2 position) {
        super(position);
        type = CellType.EMPTY;
    }
}
