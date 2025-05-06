package com.hexadeventure.model.enemies;

import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.characters.EnemySetting;
import com.hexadeventure.model.inventory.characters.Loot;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

@Getter
@Setter
public class Enemy {
    private static final double OFFSET = 0.001;
    public static final int MOVEMENT_SPEED = 2;
    
    private Vector2 position;
    private PlayableCharacter[][] enemies;
    private Loot[] loot;
    private int lootSeed;
    
    public Enemy(Vector2 position, SplittableRandom random, EnemyPattern pattern,
                 Map<WeaponType, List<WeaponSetting>> weapons) {
        this.position = position;
        this.loot = pattern.loot();
        this.lootSeed = random.nextInt(Integer.MAX_VALUE);
        
        enemies = new PlayableCharacter[GameMap.COMBAT_TERRAIN_ROW_SIZE][GameMap.COMBAT_TERRAIN_COLUMN_SIZE];
        EnemySetting[][] settings = pattern.enemies();
        for (int i = 0; i < settings.length; i++) {
            for (int j = 0; j < settings[i].length; j++) {
                if(settings[i][j] == null) continue;
                String name = settings[i][j].id();
                int health = random.nextInt(settings[i][j].minHealth(), settings[i][j].maxHealth() + 1);
                int speed = random.nextInt(settings[i][j].minSpeed(), settings[i][j].maxSpeed() + 1);
                double hypnoRes = random.nextDouble(settings[i][j].minHypnotizationResistence(),
                                                    settings[i][j].maxHypnotizationResistence() + OFFSET);
                enemies[i][j] = new PlayableCharacter(name, health, speed, hypnoRes);
                
                List<WeaponSetting> weaponSettings = weapons.get(settings[i][j].weaponType());
                WeaponSetting weaponSetting = weaponSettings.get(random.nextInt(weaponSettings.size()));
                enemies[i][j].setWeapon(new Weapon(weaponSetting, random));
            }
        }
    }
}
