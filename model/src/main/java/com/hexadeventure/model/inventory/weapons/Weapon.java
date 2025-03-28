package com.hexadeventure.model.inventory.weapons;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Weapon extends Item {
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
