package com.hexadeventure.model.inventory.initial;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitialCharacter {
    private String name;
    private int minHealth;
    private int maxHealth;
    private int minSpeed;
    private int maxSpeed;
}
