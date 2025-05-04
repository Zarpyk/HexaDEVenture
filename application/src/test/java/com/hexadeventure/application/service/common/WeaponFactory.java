package com.hexadeventure.application.service.common;

import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;

import java.util.List;
import java.util.Map;

public class WeaponFactory {
    public static final String TEST_WEAPON_NAME = "Sword";
    public static final WeaponType TEST_WEAPON_TYPE = WeaponType.MELEE;
    public static final int TEST_WEAPON_SKIN = 1;
    
    public static Weapon createWeapon() {
        return new Weapon(TEST_WEAPON_NAME, TEST_WEAPON_TYPE, TEST_WEAPON_SKIN);
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
