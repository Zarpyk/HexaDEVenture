package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.weapons.Weapon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PlayableCharacter implements Comparable<PlayableCharacter> {
    private String id;
    private String name;
    @Setter
    private double health;
    @Setter
    private double speed;
    @Setter
    private double hypnotizationResistance;
    private Weapon weapon;
    private ChangedStats changedStats;
    
    public PlayableCharacter(String name, double health, double speed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.weapon = new Weapon(Weapon.DEFAULT_WEAPON);
        
        changedStats = new ChangedStats();
        changedStats.updateStats(health, false);
    }
    
    public PlayableCharacter(String name, double health, double speed, double hypnotizationResistance) {
        this(name, health, speed);
        this.hypnotizationResistance = Math.round(hypnotizationResistance * 100) / 100d;
        
        changedStats = new ChangedStats();
        changedStats.updateStats(health, false);
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
    
    @Override
    public int compareTo(PlayableCharacter o) {
        // Sort by name alphabetically
        int compare = this.name.compareTo(o.name);
        if(compare != 0) return compare;
        // If names are equal, compare by id
        return this.id.compareTo(o.id);
    }
}
