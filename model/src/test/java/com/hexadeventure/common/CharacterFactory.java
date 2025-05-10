package com.hexadeventure.common;

import com.hexadeventure.model.inventory.characters.CharacterCombatInfo;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.weapons.Weapon;

public class CharacterFactory {
    private static final String TEST_CHARACTER_NAME = "Warrior";
    private static final int TEST_CHARACTER_HEALTH = 100;
    private static final int TEST_CHARACTER_SPEED = 10;
    private static final double TEST_CHARACTER_HYPNOTIZATION_RESISTANCE = 0;
    
    public static PlayableCharacter createCharacter() {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME,
                                                            TEST_CHARACTER_HEALTH,
                                                            TEST_CHARACTER_SPEED,
                                                            TEST_CHARACTER_HYPNOTIZATION_RESISTANCE);
        Weapon weapon = WeaponFactory.createWeapon();
        character.setWeapon(weapon);
        return character;
    }
    
    public static CharacterCombatInfo createCombatCharacter() {
        return new CharacterCombatInfo(createCharacter(), 0, 0, false);
    }
}
