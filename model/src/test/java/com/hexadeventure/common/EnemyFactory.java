package com.hexadeventure.common;

import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.characters.EnemySetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;

public class EnemyFactory {
    public static final String TEST_ENEMY_ID = "TestEnemy";
    public static final WeaponType TEST_WEAPON_TYPE = WeaponType.MELEE;
    public static final int TEST_MIN_HEALTH = 1;
    public static final int TEST_MAX_HEALTH = 2;
    public static final int TEST_MIN_SPEED = 1;
    public static final int TEST_MAX_SPEED = 2;
    public static final int TEST_MIN_HYPNOTIZATION_RESISTENCE = 1;
    public static final int TEST_MAX_HYPNOTIZATION_RESISTENCE = 2;
    
    public static EnemySetting createEnemySetting() {
        return new EnemySetting(
                TEST_ENEMY_ID,
                TEST_WEAPON_TYPE,
                TEST_MIN_HEALTH,
                TEST_MAX_HEALTH,
                TEST_MIN_SPEED,
                TEST_MAX_SPEED,
                TEST_MIN_HYPNOTIZATION_RESISTENCE,
                TEST_MAX_HYPNOTIZATION_RESISTENCE
        );
    }
    
    public static EnemyPattern createEnemyPattern() {
        return new EnemyPattern(0, new EnemySetting[][]{
                {createEnemySetting(), null, null, null},
                {createEnemySetting(), null, null, null},
                {createEnemySetting(), null, null, null},
                });
    }
}
