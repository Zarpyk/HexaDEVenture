package com.hexadeventure.model.map.empty;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmptyCell extends CellData {
    private EmptyCellType emptyCellType;
    
    public EmptyCell(Vector2 position) {
        super(position);
        type = CellType.EMPTY;
        emptyCellType = EmptyCellType.GROUND;
    }
    
    public EmptyCell(Vector2 position, EmptyCellType emptyCellType) {
        super(position);
        type = CellType.EMPTY;
        this.emptyCellType = emptyCellType;
    }
}
