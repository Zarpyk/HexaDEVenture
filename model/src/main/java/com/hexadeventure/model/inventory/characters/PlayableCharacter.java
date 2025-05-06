package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.weapons.Weapon;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class PlayableCharacter {
    private final String id;
    private final String name;
    @Setter
    private double health;
    @Setter
    private double speed;
    @Setter
    private double hypnotizationResistence;
    private Weapon weapon;
    private final ChangedStats changedStats;
    
    public PlayableCharacter(String name, double health, double speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.weapon = new Weapon(Weapon.DEFAULT_WEAPON);
        
        changedStats = new ChangedStats();
        changedStats.updateStats(health, false);
    }
    
    public PlayableCharacter(String name, double health, double speed, double hypnotizationResistence) {
        this(name, health, speed);
        this.hypnotizationResistence = Math.round(hypnotizationResistence * 100) / 100d;
    }
    
    public void setWeapon(Weapon weapon) {
        this.weapon = Objects.requireNonNullElse(weapon, new Weapon(Weapon.DEFAULT_WEAPON));
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof PlayableCharacter that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
