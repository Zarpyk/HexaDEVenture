package com.hexadeventure.common;

import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;

import java.util.List;
import java.util.Map;

public class WeaponFactory {
    public static final String TEST_WEAPON_NAME = "Weapon";
    public static final WeaponType TEST_WEAPON_TYPE = WeaponType.MELEE;
    public static final int TEST_WEAPON_SKIN = 1;
    
    public static final double TEST_DAMAGE = 10;
    public static final double TEST_MELEE_DEFENSE = 5;
    public static final double TEST_RANGED_DEFENSE = 5;
    public static final int TEST_COOLDOWN = 1;
    public static final double TEST_AGGRO_GENERATION = 50;
    public static final int TEST_INITIAL_AGGRO = 50;
    public static final double TEST_HEALING_POWER = 0;
    public static final double TEST_HEALER_HEALING_POWER = 10;
    public static final double TEST_HYPNOTIZATION_POWER = 0;
    public static final double TEST_HYPNOTIZER_HYPNOTIZATION_POWER = 10;
    
    public static Weapon createWeapon() {
        return new Weapon(TEST_WEAPON_NAME, TEST_WEAPON_TYPE, TEST_WEAPON_SKIN,
                          TEST_DAMAGE, TEST_MELEE_DEFENSE, TEST_RANGED_DEFENSE,
                          TEST_COOLDOWN, TEST_AGGRO_GENERATION, TEST_INITIAL_AGGRO,
                          TEST_HEALING_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static Weapon createMeleeWeapon() {
        return createWeapon();
    }
    
    public static Weapon createRangedWeapon() {
        return new Weapon(TEST_WEAPON_NAME, WeaponType.RANGED, TEST_WEAPON_SKIN,
                          TEST_DAMAGE, TEST_MELEE_DEFENSE, TEST_RANGED_DEFENSE,
                          TEST_COOLDOWN, TEST_AGGRO_GENERATION, TEST_INITIAL_AGGRO,
                          TEST_HEALING_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static Weapon createTankWeapon() {
        return new Weapon(TEST_WEAPON_NAME, WeaponType.TANK, TEST_WEAPON_SKIN,
                          TEST_DAMAGE, TEST_MELEE_DEFENSE, TEST_RANGED_DEFENSE,
                          TEST_COOLDOWN, TEST_AGGRO_GENERATION, TEST_INITIAL_AGGRO,
                          TEST_HEALING_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static Weapon createHealerWeapon() {
        return new Weapon(TEST_WEAPON_NAME, WeaponType.HEALER, TEST_WEAPON_SKIN,
                          TEST_DAMAGE, TEST_MELEE_DEFENSE, TEST_RANGED_DEFENSE,
                          TEST_COOLDOWN, TEST_AGGRO_GENERATION, TEST_INITIAL_AGGRO,
                          TEST_HEALER_HEALING_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static Weapon createHypnotizerWeapon() {
        return new Weapon(TEST_WEAPON_NAME, WeaponType.HYPNOTIZER, TEST_WEAPON_SKIN,
                          TEST_DAMAGE, TEST_MELEE_DEFENSE, TEST_RANGED_DEFENSE,
                          TEST_COOLDOWN, TEST_AGGRO_GENERATION, TEST_INITIAL_AGGRO,
                          TEST_HEALING_POWER, TEST_HYPNOTIZER_HYPNOTIZATION_POWER);
    }
    
    public static Map<WeaponType, List<WeaponSetting>> createWeaponsSettings() {
        return Map.of(
                WeaponType.MELEE, List.of(createWeaponSetting())
        );
    }
    
    public static WeaponSetting createWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME,
                                 0,
                                 TEST_WEAPON_TYPE,
                                 0,
                                 1,
                                 0,
                                 1,
                                 0,
                                 1,
                                 0,
                                 1,
                                 AggroGenType.ATTACK,
                                 0,
                                 0,
                                 1,
                                 0,
                                 0,
                                 1,
                                 0,
                                 1);
    }
}
