package com.hexadeventure.application.service.common;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.weapons.Weapon;

public class PlayableCharacterFactory {
    public static final int TEST_CHARACTER_HEALTH = 100;
    public static final String TEST_CHARACTER_NAME = "Test Character";
    
    public static PlayableCharacter createMeleeCharacter(int speed) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        character.setWeapon(WeaponFactory.createMeleeWeapon());
        return character;
    }
    
    public static PlayableCharacter createMeleeCharacter(int speed, int aggro) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        Weapon weapon = WeaponFactory.createMeleeWeapon();
        weapon.setInitialAggro(aggro);
        character.setWeapon(weapon);
        return character;
    }
    
    public static PlayableCharacter createRangedCharacter(int speed) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        character.setWeapon(WeaponFactory.createRangedWeapon());
        return character;
    }
    
    public static PlayableCharacter createTankCharacter(int speed) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        character.setWeapon(WeaponFactory.createTankWeapon());
        return character;
    }
    
    public static PlayableCharacter createHealerCharacter(int speed) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        character.setWeapon(WeaponFactory.createHealerWeapon());
        return character;
    }
    
    public static PlayableCharacter createHypnotizerCharacter(int speed) {
        PlayableCharacter character = new PlayableCharacter(TEST_CHARACTER_NAME, TEST_CHARACTER_HEALTH, speed);
        character.setWeapon(WeaponFactory.createHypnotizerWeapon());
        return character;
    }
}
