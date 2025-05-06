package com.hexadeventure.adapter.out.settings.json.enemy;

import com.hexadeventure.model.inventory.characters.EnemySetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class EnemyPatternJson {
    private float minThreshold;
    private String[][] enemies;
    private LootJson[] loot;
    
    public EnemySetting[][] toModel(Map<String, EnemySetting> enemySettings, int rowSize, int columnSize) {
        EnemySetting[][] model = new EnemySetting[rowSize][columnSize];
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < enemies[i].length; j++) {
                if(enemies[i][j] != null) {
                    model[i][j] = enemySettings.get(enemies[i][j]);
                }
            }
        }
        return model;
    }
}
