package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.weapons.WeaponType;

public record EnemySetting(
        String id,
        WeaponType weaponType,
        int minHealth,
        int maxHealth,
        int minSpeed,
        int maxSpeed
) {}
