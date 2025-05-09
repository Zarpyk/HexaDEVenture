package com.hexadeventure.model.inventory.characters;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChangedStats {
    private double health;
    private boolean hypnotized;
    
    @Setter
    private double boostHealth;
    @Setter
    private double boostSpeed;
    @Setter
    private double boostStrength;
    @Setter
    private double boostDefense;
    
    public void updateStats(double health, boolean hypnotized) {
        this.health = health;
        this.hypnotized = hypnotized;
    }
    
    public void updateStats(CharacterCombatInfo characterCombatInfo) {
        // Reset boost if the character has more health than normal
        double normalHealth = characterCombatInfo.getCharacter().getHealth();
        double currentHealth = characterCombatInfo.getHealth();
        this.health = Math.min(normalHealth, currentHealth);
        
        this.hypnotized = characterCombatInfo.isHypnotized();
    }
    
    public void heal(double max, double healthPoints) {
        health += healthPoints;
        health = Math.min(health, max);
    }
    
    public void resetBoosts() {
        this.boostHealth = 0;
        this.boostSpeed = 0;
        this.boostStrength = 0;
        this.boostDefense = 0;
    }
}
