package com.hexadeventure.model.inventory.weapons;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.SplittableRandom;

@Getter
@Setter
public class Weapon extends Item {
    private static final double OFFSET = 0.001;
    
    private WeaponType weaponType;
    private double damage;
    private double meleeDefense;
    private double rangedDefense;
    private int cooldown;
    private double aggroGeneration;
    private int initialAggro;
    private double healingPower;
    private double hipnotizationPower;
    
    public Weapon(String name, WeaponType weaponType, int skin) {
        super(name, ItemType.WEAPON, skin);
        this.weaponType = weaponType;
        setId(Integer.toString(hashCode()));
    }
    
    public Weapon(WeaponData weaponData, SplittableRandom random) {
        super(weaponData.name(), ItemType.WEAPON, weaponData.skin());
        weaponType = weaponData.weaponType();
        damage = Math.round(random.nextDouble(weaponData.minDamage(),
                                              weaponData.maxDamage() + OFFSET) * 100) / 100d;
        meleeDefense = Math.round(random.nextDouble(weaponData.minMeleeDefense(),
                                                    weaponData.maxMeleeDefense() + OFFSET) * 100) / 100d;
        rangedDefense = Math.round(random.nextDouble(weaponData.minRangedDefense(),
                                                     weaponData.maxRangedDefense() + OFFSET) * 100) / 100d;
        initialAggro = weaponData.initialAggro();
        healingPower = Math.round(random.nextDouble(weaponData.minHealingPower(),
                                                    weaponData.maxHealingPower() + OFFSET) * 100) / 100d;
        hipnotizationPower = Math.round(random.nextDouble(weaponData.minHipnotizationPower(),
                                                          weaponData.maxHipnotizationPower() + OFFSET) * 100) / 100d;
        cooldown = initCooldown(weaponData, random);
        aggroGeneration = initAggroGeneration(weaponData, random);
        setId(Integer.toString(hashCode()));
    }
    
    public Weapon(String name, WeaponType weaponType, int skin, double damage, double meleeDefense,
                  double rangedDefense, int cooldown, double aggroGeneration, int initialAggro, double healingPower,
                  double hipnotizationPower) {
        super(name, ItemType.WEAPON, skin);
        this.weaponType = weaponType;
        this.damage = damage;
        this.meleeDefense = meleeDefense;
        this.rangedDefense = rangedDefense;
        this.cooldown = cooldown;
        this.aggroGeneration = aggroGeneration;
        this.initialAggro = initialAggro;
        this.healingPower = healingPower;
        this.hipnotizationPower = hipnotizationPower;
        setId(Integer.toString(hashCode()));
    }
    
    private int initCooldown(WeaponData weaponData, SplittableRandom random) {
        return switch (weaponType) {
            case MELEE, RANGED, TANK -> random.nextInt(weaponData.minCooldown(),
                                                       weaponData.maxCooldown() + 1);
            case HEALER -> (int) Math.round(random.nextInt(weaponData.minCooldown(),
                                                           weaponData.maxCooldown() + 1) *
                                            (healingPower / weaponData.maxHealingPower()));
            case HIPNOTIZER -> (int) Math.round(random.nextInt(weaponData.minCooldown(),
                                                               weaponData.maxCooldown() + 1) *
                                                (hipnotizationPower / weaponData.maxHipnotizationPower()));
        };
    }
    
    private double initAggroGeneration(WeaponData weaponData, SplittableRandom random) {
        return switch (weaponData.aggroGenType()) {
            case ATTACK -> damage;
            case ATTACK_AND_EXTRA -> damage + weaponData.extraAggroGeneration();
            case RANGE -> Math.round(random.nextDouble(weaponData.minAggroGeneration(),
                                                       weaponData.maxAggroGeneration() + OFFSET) * 100) / 100d;
            case HEALING -> healingPower;
            case HEALING_AND_EXTRA -> healingPower + weaponData.extraAggroGeneration();
            case HIPNOTIZATION -> hipnotizationPower;
        };
    }
    
    @Override
    public String toString() {
        return super.toString() + "-" + hashCode();
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
                            hipnotizationPower);
    }
}
