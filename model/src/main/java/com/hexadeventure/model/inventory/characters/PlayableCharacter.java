package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.weapons.Weapon;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class PlayableCharacter {
    private final String id;
    private final String name;
    private final int health;
    private final int speed;
    @Setter
    private Weapon weapon;
    
    public PlayableCharacter(String name, int health, int speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.health = health;
        this.speed = speed;
    }
}
