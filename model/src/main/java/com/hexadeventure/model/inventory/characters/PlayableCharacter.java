package com.hexadeventure.model.inventory.characters;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayableCharacter {
    private final String id;
    private final String name;
    private final int health;
    private final int speed;
    
    public PlayableCharacter(String name, int health, int speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.health = health;
        this.speed = speed;
    }
}
