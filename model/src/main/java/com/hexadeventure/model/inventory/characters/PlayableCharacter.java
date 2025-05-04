package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class PlayableCharacter {
    private static final Weapon DEFAULT_WEAPON = new Weapon("Fists",
                                                            WeaponType.MELEE,
                                                            -1,
                                                            1,
                                                            1,
                                                            1,
                                                            1,
                                                            1,
                                                            100,
                                                            0,
                                                            0);
    
    private final String id;
    private final String name;
    private final int health;
    private final int speed;
    private Weapon weapon;
    
    public PlayableCharacter(String name, int health, int speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.weapon = new Weapon(DEFAULT_WEAPON);
    }
    
    public void setWeapon(Weapon weapon) {
        this.weapon = Objects.requireNonNullElse(weapon, new Weapon(DEFAULT_WEAPON));
    }
}
