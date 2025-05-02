package com.hexadeventure.common;

import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponType;

public class WeaponFactory {
    public static final String TEST_WEAPON_NAME = "Sword";
    public static final WeaponType TEST_WEAPON_TYPE = WeaponType.MELEE;
    public static final int TEST_WEAPON_SKIN = 1;
    
    public static Weapon createWeapon() {
        return new Weapon(TEST_WEAPON_NAME, TEST_WEAPON_TYPE, TEST_WEAPON_SKIN);
    }
}
