package com.hexadeventure.model.inventory.characters;

import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;

import java.util.Objects;

@Getter
@SuppressWarnings("FieldMayBeFinal")
public class CharacterCombatInfo {
    // Create a copy of PlayableCharacter to avoid modifying the original character
    // This allows modifying the stats of the character in combat
    
    public static final double FIRST_ROW_AGGRO = 1.5;
    public static final double SECOND_ROW_AGGRO = 0.75;
    public static final double THIRD_ROW_AGGRO = 0.5;
    
    public static final double SECOND_ROW_SPEED = 0.75;
    public static final double THIRD_ROW_SPEED = 0.5;
    
    public static final double MAX_DEFENSE = 75;
    
    private PlayableCharacter character;
    private final int row;
    private final int column;
    private final boolean isEnemy;
    
    private final String id;
    private final String name;
    private double health;
    private double speed;
    private double hypnotizationResistance;
    private boolean isHypnotized;
    
    private WeaponType weaponType;
    private double damage;
    private double meleeDefense;
    private double rangedDefense;
    private int cooldown;
    private double aggroGeneration;
    private double currentAggro;
    private double healingPower;
    private double hypnotizationPower;
    
    public CharacterCombatInfo(PlayableCharacter character, int row, int column, boolean isEnemy) {
        this.character = character;
        this.row = row;
        this.column = column;
        this.isEnemy = isEnemy;
        
        this.id = character.getId();
        this.name = character.getName();
        this.health = character.getChangedStats().getHealth() + character.getChangedStats().getBoostHealth();
        this.speed = character.getSpeed() + character.getChangedStats().getBoostSpeed();
        this.hypnotizationResistance = character.getHypnotizationResistance();
        this.isHypnotized = character.getChangedStats().isHypnotized();
        this.weaponType = character.getWeapon().getWeaponType();
        this.damage = character.getWeapon().getDamage() + character.getChangedStats().getBoostStrength();
        this.meleeDefense = character.getWeapon().getMeleeDefense() + character.getChangedStats().getBoostDefense();
        this.meleeDefense = Math.min(this.meleeDefense, MAX_DEFENSE);
        this.rangedDefense = character.getWeapon().getRangedDefense() + character.getChangedStats().getBoostDefense();
        this.rangedDefense = Math.min(this.rangedDefense, MAX_DEFENSE);
        this.cooldown = 0;
        this.aggroGeneration = character.getWeapon().getAggroGeneration();
        this.currentAggro = character.getWeapon().getInitialAggro();
        this.healingPower = character.getWeapon().getHealingPower();
        this.hypnotizationPower = character.getWeapon().getHypnotizationPower();
        
        if(row == 0) {
            this.currentAggro *= FIRST_ROW_AGGRO;
        } else if(row == 1) {
            this.currentAggro *= SECOND_ROW_AGGRO;
            this.speed *= SECOND_ROW_SPEED;
        } else if(row == 2) {
            this.currentAggro *= THIRD_ROW_AGGRO;
            this.speed *= THIRD_ROW_SPEED;
        }
    }
    
    /**
     * <p>Compares this character's speed with another character's speed.</p>
     * <p>If the target speed is higher, it will be prioritized.</p>
     * <p>If the speed is the same, it will prioritize the ally.</p>
     * <p>If the row is the same, it will prioritize the lower row.</p>
     * <p>If the row is the same, it will prioritize the lower column.</p>
     *
     * @param o the other `CharacterCombatInfo` to compare with
     * @return a negative integer, zero, or a positive integer.
     */
    public int compareBySpeed(CharacterCombatInfo o) {
        // Prioritize higher speed
        int compare = Double.compare(o.getSpeed(), getSpeed());
        if(compare != 0) return compare;
        // If the speed is the same, prioritize ally
        if(!this.isEnemy && o.isEnemy) return -1;
        if(this.isEnemy && !o.isEnemy) return 1;
        // After that, prioritize the lower row
        compare = Integer.compare(this.row, o.row);
        if(compare != 0) return compare;
        // Then, prioritize the lower column
        return Integer.compare(this.column, o.column);
    }
    
    /**
     * <p>Compares this character's aggro with another character's aggro.</p>
     * <p>If the target aggro is higher, it will be prioritized.</p>
     * <p>If the aggro is the same, it will prioritize the lower row.</p>
     * <p>If the row is the same, it will prioritize the lower column.</p>
     * <p>This comparison doesn't take into account if this is an enemy or ally.</p>
     *
     * @param o the other `CharacterCombatInfo` to compare with
     * @return a negative integer, zero, or a positive integer.
     */
    public int compareByAggro(CharacterCombatInfo o) {
        // Prioritize higher aggro
        int compare = Double.compare(o.getCurrentAggro(), getCurrentAggro());
        if(compare != 0) return compare;
        // If the aggro is the same, prioritize the lower row
        compare = Integer.compare(this.row, o.row);
        if(compare != 0) return compare;
        // Then, prioritize the lower column
        return Integer.compare(this.column, o.column);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof CharacterCombatInfo that)) return false;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    public CharacterStatusChange damage(double damage) {
        if(isDead()) return null;
        double oldHealth = this.health;
        this.health -= damage;
        if(this.health < 0) this.health = 0;
        return new CharacterStatusChange(CharacterStat.HEALTH, oldHealth, this.health);
    }
    
    public CharacterStatusChange heal(double heal) {
        if(isDead()) return null;
        double oldHealth = this.health;
        this.health += heal;
        if(this.health > character.getHealth()) this.health = character.getHealth();
        return new CharacterStatusChange(CharacterStat.HEALTH, oldHealth, this.health);
    }
    
    public boolean isDead() {
        return this.health <= 0 || this.isHypnotized;
    }
    
    public CharacterStatusChange reduceCooldown() {
        if(isDead()) return null;
        if(this.cooldown <= 0) return null;
        int oldCooldown = this.cooldown;
        this.cooldown--;
        return new CharacterStatusChange(CharacterStat.COOLDOWN, oldCooldown, this.cooldown);
    }
    
    public CharacterStatusChange resetCooldown() {
        if(isDead()) return null;
        this.cooldown = character.getWeapon().getCooldown();
        return new CharacterStatusChange(CharacterStat.COOLDOWN, 0, this.cooldown);
    }
    
    public CharacterStatusChange increaseAggro(double aggro) {
        if(isDead()) return null;
        double oldAggro = this.currentAggro;
        this.currentAggro += aggro;
        return new CharacterStatusChange(CharacterStat.CURRENT_AGGRO, oldAggro, this.currentAggro);
    }
    
    public CharacterStatusChange hypnotize() {
        if(isDead()) return null;
        this.isHypnotized = true;
        return new CharacterStatusChange(CharacterStat.HYPNOTIZED, 0, 1);
    }
}
