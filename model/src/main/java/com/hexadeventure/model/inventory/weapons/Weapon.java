package com.hexadeventure.model.inventory.weapons;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.SplittableRandom;

@Getter
@Setter
@NoArgsConstructor
public class Weapon extends Item {
    private static final double OFFSET = 0.001;
    
    public static final Weapon DEFAULT_WEAPON = new Weapon("Fists",
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
    
    private WeaponType weaponType;
    private double damage;
    private double meleeDefense;
    private double rangedDefense;
    private int cooldown;
    private double aggroGeneration;
    private int initialAggro;
    private double healingPower;
    private double hypnotizationPower;
    
    public Weapon(Weapon weapon) {
        super(weapon.getName(), ItemType.WEAPON, weapon.getSkin());
        this.weaponType = weapon.weaponType;
        this.damage = weapon.damage;
        this.meleeDefense = weapon.meleeDefense;
        this.rangedDefense = weapon.rangedDefense;
        this.cooldown = weapon.cooldown;
        this.aggroGeneration = weapon.aggroGeneration;
        this.initialAggro = weapon.initialAggro;
        this.healingPower = weapon.healingPower;
        this.hypnotizationPower = weapon.hypnotizationPower;
        setId(weapon.getId());
    }
    
    public Weapon(String name, WeaponType weaponType, int skin) {
        super(name, ItemType.WEAPON, skin);
        this.weaponType = weaponType;
        setId(Integer.toString(hashCode()));
    }
    
    public Weapon(WeaponSetting weaponSetting, SplittableRandom random) {
        super(weaponSetting.name(), ItemType.WEAPON, weaponSetting.skin());
        weaponType = weaponSetting.weaponType();
        damage = Math.round(random.nextDouble(weaponSetting.minDamage(),
                                              weaponSetting.maxDamage() + OFFSET) * 100) / 100d;
        meleeDefense = Math.round(random.nextDouble(weaponSetting.minMeleeDefense(),
                                                    weaponSetting.maxMeleeDefense() + OFFSET) * 100) / 100d;
        rangedDefense = Math.round(random.nextDouble(weaponSetting.minRangedDefense(),
                                                     weaponSetting.maxRangedDefense() + OFFSET) * 100) / 100d;
        initialAggro = weaponSetting.initialAggro();
        healingPower = Math.round(random.nextDouble(weaponSetting.minHealingPower(),
                                                    weaponSetting.maxHealingPower() + OFFSET) * 100) / 100d;
        hypnotizationPower = Math.round(random.nextDouble(weaponSetting.minHypnotizationPower(),
                                                          weaponSetting.maxHypnotizationPower() + OFFSET) * 100) / 100d;
        cooldown = initCooldown(weaponSetting, random);
        aggroGeneration = initAggroGeneration(weaponSetting, random);
        setId(Integer.toString(hashCode()));
    }
    
    public Weapon(String name, WeaponType weaponType, int skin, double damage, double meleeDefense,
                  double rangedDefense, int cooldown, double aggroGeneration, int initialAggro, double healingPower,
                  double hypnotizationPower) {
        super(name, ItemType.WEAPON, skin);
        this.weaponType = weaponType;
        this.damage = damage;
        this.meleeDefense = meleeDefense;
        this.rangedDefense = rangedDefense;
        this.cooldown = cooldown;
        this.aggroGeneration = aggroGeneration;
        this.initialAggro = initialAggro;
        this.healingPower = healingPower;
        this.hypnotizationPower = hypnotizationPower;
        setId(Integer.toString(hashCode()));
    }
    
    private int initCooldown(WeaponSetting weaponSetting, SplittableRandom random) {
        return switch (weaponType) {
            case MELEE, RANGED, TANK -> random.nextInt(weaponSetting.minCooldown(),
                                                       weaponSetting.maxCooldown() + 1);
            case HEALER -> (int) Math.round(random.nextInt(weaponSetting.minCooldown(),
                                                           weaponSetting.maxCooldown() + 1) *
                                            (healingPower / weaponSetting.maxHealingPower()));
            case HYPNOTIZER -> (int) Math.round(random.nextInt(weaponSetting.minCooldown(),
                                                               weaponSetting.maxCooldown() + 1) *
                                                (hypnotizationPower / weaponSetting.maxHypnotizationPower()));
        };
    }
    
    private double initAggroGeneration(WeaponSetting weaponSetting, SplittableRandom random) {
        return switch (weaponSetting.aggroGenType()) {
            case ATTACK -> damage;
            case ATTACK_AND_EXTRA -> damage + weaponSetting.extraAggroGeneration();
            case RANGE -> Math.round(random.nextDouble(weaponSetting.minAggroGeneration(),
                                                       weaponSetting.maxAggroGeneration() + OFFSET) * 100) / 100d;
            case HEALING -> healingPower;
            case HEALING_AND_EXTRA -> healingPower + weaponSetting.extraAggroGeneration();
            case HYPNOTIZATION -> hypnotizationPower;
        };
    }
    
    @Override
    public String toString() {
        return super.toString() + "-" + hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(getId(), weapon.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                            weaponType,
                            damage,
                            meleeDefense,
                            rangedDefense,
                            cooldown,
                            aggroGeneration,
                            initialAggro,
                            healingPower,
                            hypnotizationPower);
    }
}
