package com.hexadeventure.common;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.weapons.Weapon;

public class CharacterFactory {
    private static final String TEST_CHARACTER_NAME = "Warrior";
    private static final int TEST_CHARACTER_HEALTH = 100;
    private static final int TEST_CHARACTER_SPEED = 10;
    
    public static PlayableCharacter createCharacter() {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                            TEST_CHARACTER_HEALTH,
                                                            TEST_CHARACTER_SPEED);
        Weapon weapon = WeaponFactory.createWeapon();
        character.setWeapon(weapon);
        return character;
    }
}
