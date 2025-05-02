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
        hipnotizationPower = Math.round(random.nextDouble(weaponSetting.minHipnotizationPower(),
                                                          weaponSetting.maxHipnotizationPower() + OFFSET) * 100) / 100d;
        cooldown = initCooldown(weaponSetting, random);
        aggroGeneration = initAggroGeneration(weaponSetting, random);
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
    
    private int initCooldown(WeaponSetting weaponSetting, SplittableRandom random) {
        return switch (weaponType) {
            case MELEE, RANGED, TANK -> random.nextInt(weaponSetting.minCooldown(),
                                                       weaponSetting.maxCooldown() + 1);
            case HEALER -> (int) Math.round(random.nextInt(weaponSetting.minCooldown(),
                                                           weaponSetting.maxCooldown() + 1) *
                                            (healingPower / weaponSetting.maxHealingPower()));
            case HIPNOTIZER -> (int) Math.round(random.nextInt(weaponSetting.minCooldown(),
                                                               weaponSetting.maxCooldown() + 1) *
                                                (hipnotizationPower / weaponSetting.maxHipnotizationPower()));
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
