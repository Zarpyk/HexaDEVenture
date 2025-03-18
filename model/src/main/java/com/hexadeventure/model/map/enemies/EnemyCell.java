package com.hexadeventure.model.map.enemies;

import lombok.Getter;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.Vector2;

@Getter
public class EnemyCell extends CellData {
    private Enemy enemy;
    
    public EnemyCell(Vector2 position, double threshold) {
        super(position);
        type = CellType.ENEMY;
        // TODO: Implement this
    }
    
    public EnemyCell(Vector2 position, Enemy enemy) {
        super(position);
        type = CellType.ENEMY;
        this.enemy = enemy;
    }
}
