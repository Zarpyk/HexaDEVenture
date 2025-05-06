package com.hexadeventure.application.service.common;

import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class WeaponFactory {
    public static final int TEST_SEED = 1234;
    
    public static final String TEST_WEAPON_NAME = "Sword";
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
    public static final double TEST_HYPNOTIZER_HYPNOTIZATION_POWER = 100;
    
    public static Weapon createWeapon() {
        return new Weapon(createMeleeWeaponSetting(),
                          new SplittableRandom(TEST_SEED));
    }
    
    public static Weapon createMeleeWeapon() {
        return createWeapon();
    }
    
    public static Weapon createRangedWeapon() {
        return new Weapon(createRangedWeaponSetting(),
                          new SplittableRandom(TEST_SEED));
    }
    
    public static Weapon createTankWeapon() {
        return new Weapon(createTankWeaponSetting(),
                          new SplittableRandom(TEST_SEED));
    }
    
    public static Weapon createHealerWeapon() {
        return new Weapon(createHealerWeaponSetting(),
                          new SplittableRandom(TEST_SEED));
    }
    
    public static Weapon createHypnotizerWeapon() {
        return new Weapon(createHypnotizerWeaponSetting(),
                          new SplittableRandom(TEST_SEED));
    }
    
    public static Map<WeaponType, List<WeaponSetting>> createWeaponsSettings() {
        return Map.of(
                WeaponType.MELEE, List.of(createMeleeWeaponSetting())
        );
    }
    
    public static WeaponSetting createMeleeWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME, 0, 0, 1, TEST_WEAPON_TYPE,
                                 TEST_DAMAGE, TEST_DAMAGE,
                                 TEST_MELEE_DEFENSE, TEST_MELEE_DEFENSE,
                                 TEST_RANGED_DEFENSE, TEST_RANGED_DEFENSE,
                                 TEST_COOLDOWN, TEST_COOLDOWN,
                                 AggroGenType.ATTACK,
                                 TEST_AGGRO_GENERATION, TEST_AGGRO_GENERATION,
                                 TEST_INITIAL_AGGRO, TEST_INITIAL_AGGRO,
                                 TEST_HEALING_POWER, TEST_HEALING_POWER,
                                 TEST_HYPNOTIZATION_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static WeaponSetting createRangedWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME, 0, 0, 1, WeaponType.RANGED,
                                 TEST_DAMAGE, TEST_DAMAGE,
                                 TEST_MELEE_DEFENSE, TEST_MELEE_DEFENSE,
                                 TEST_RANGED_DEFENSE, TEST_RANGED_DEFENSE,
                                 TEST_COOLDOWN, TEST_COOLDOWN,
                                 AggroGenType.ATTACK_AND_EXTRA,
                                 TEST_AGGRO_GENERATION, TEST_AGGRO_GENERATION,
                                 TEST_INITIAL_AGGRO, TEST_INITIAL_AGGRO,
                                 TEST_HEALING_POWER, TEST_HEALING_POWER,
                                 TEST_HYPNOTIZATION_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static WeaponSetting createTankWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME, 0, 0, 1, WeaponType.TANK,
                                 TEST_DAMAGE, TEST_DAMAGE,
                                 TEST_MELEE_DEFENSE, TEST_MELEE_DEFENSE,
                                 TEST_RANGED_DEFENSE, TEST_RANGED_DEFENSE,
                                 TEST_COOLDOWN, TEST_COOLDOWN,
                                 AggroGenType.RANGE,
                                 TEST_AGGRO_GENERATION, TEST_AGGRO_GENERATION,
                                 TEST_INITIAL_AGGRO, TEST_INITIAL_AGGRO,
                                 TEST_HEALING_POWER, TEST_HEALING_POWER,
                                 TEST_HYPNOTIZATION_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static WeaponSetting createHealerWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME, 0, 0, 1, WeaponType.HEALER,
                                 TEST_DAMAGE, TEST_DAMAGE,
                                 TEST_MELEE_DEFENSE, TEST_MELEE_DEFENSE,
                                 TEST_RANGED_DEFENSE, TEST_RANGED_DEFENSE,
                                 TEST_COOLDOWN, TEST_COOLDOWN,
                                 AggroGenType.HEALING_AND_EXTRA,
                                 TEST_AGGRO_GENERATION, TEST_AGGRO_GENERATION,
                                 TEST_INITIAL_AGGRO, TEST_INITIAL_AGGRO,
                                 TEST_HEALER_HEALING_POWER, TEST_HEALER_HEALING_POWER,
                                 TEST_HYPNOTIZATION_POWER, TEST_HYPNOTIZATION_POWER);
    }
    
    public static WeaponSetting createHypnotizerWeaponSetting() {
        return new WeaponSetting(TEST_WEAPON_NAME, 0, 0, 1, WeaponType.HYPNOTIZER,
                                 TEST_DAMAGE, TEST_DAMAGE,
                                 TEST_MELEE_DEFENSE, TEST_MELEE_DEFENSE,
                                 TEST_RANGED_DEFENSE, TEST_RANGED_DEFENSE,
                                 TEST_COOLDOWN, TEST_COOLDOWN,
                                 AggroGenType.HYPNOTIZATION,
                                 TEST_AGGRO_GENERATION, TEST_AGGRO_GENERATION,
                                 TEST_INITIAL_AGGRO, TEST_INITIAL_AGGRO,
                                 TEST_HEALING_POWER, TEST_HEALING_POWER,
                                 TEST_HYPNOTIZER_HYPNOTIZATION_POWER, TEST_HYPNOTIZER_HYPNOTIZATION_POWER);
    }
}
