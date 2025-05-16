package com.hexadeventure.model.enemies;

import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.Vector2;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class Boss extends Enemy {
    // Splitting boss from a normal enemy allow difference it on the printMap
    public Boss(Vector2 position, SplittableRandom random, EnemyPattern enemyPatterns,
                Map<WeaponType, List<WeaponSetting>> weapons) {
        super(position, random, enemyPatterns, weapons);
    }
}
