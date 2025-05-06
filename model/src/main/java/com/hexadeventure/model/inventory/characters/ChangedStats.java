package com.hexadeventure.model.inventory.characters;

import lombok.Getter;

@Getter
public class ChangedStats {
    private double health;
    private boolean hypnotized;
    
    public void updateStats(double health, boolean hypnotized) {
        this.health = health;
        this.hypnotized = hypnotized;
    }
    
    public void updateStats(CharacterCombatInfo characterCombatInfo) {
        this.health = characterCombatInfo.getHealth();
        this.hypnotized = characterCombatInfo.isHypnotized();
    }
}
