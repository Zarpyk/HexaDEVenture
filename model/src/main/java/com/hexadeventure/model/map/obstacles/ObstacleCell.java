package com.hexadeventure.model.map.obstacles;

import lombok.Getter;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.Vector2;

@Getter
public class ObstacleCell extends CellData {
    private final ObstacleType obstacleType;
    
    public ObstacleCell(Vector2 position, double threshold) {
        super(position);
        type = CellType.OBSTACLE;
        // TODO: Implement this
        if (threshold >= 0){
            this.obstacleType = ObstacleType.WALL;
        } else {
            this.obstacleType = null;
        }
    }
    
    public ObstacleCell(Vector2 position, ObstacleType obstacleType) {
        super(position);
        type = CellType.OBSTACLE;
        this.obstacleType = obstacleType;
    }
}
