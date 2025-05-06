package com.hexadeventure.adapter.out.settings.json.weapon;

import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeaponDataJson extends ItemJson<WeaponSetting> {
    private double minThreshold;
    private double maxThreshold;
    
    private WeaponType weaponType;
    
    private double minDamage;
    private double maxDamage;
    
    private double minMeleeDefense;
    private double maxMeleeDefense;
    
    private double minRangedDefense;
    private double maxRangedDefense;
    
    private int minCooldown;
    private int maxCooldown;
    
    private AggroGenType aggroGenType;
    private double extraAggroGeneration;
    private double minAggroGeneration;
    private double maxAggroGeneration;
    
    private int initialAggro;
    
    private double minHealingPower;
    private double maxHealingPower;
    private double minHypnotizationPower;
    private double maxHypnotizationPower;
    
    public static String getID(WeaponDataJson weaponDataJson) {
        return weaponDataJson.getName();
    }
    
    public WeaponSetting toModel() {
        return new WeaponSetting(getName(),
                                 getSkin(),
                                 minThreshold,
                                 maxThreshold,
                                 weaponType,
                                 minDamage,
                                 maxDamage,
                                 minMeleeDefense,
                                 maxMeleeDefense,
                                 minRangedDefense,
                                 maxRangedDefense,
                                 minCooldown,
                                 maxCooldown,
                                 AggroGenType.values()[aggroGenType.ordinal()],
                                 extraAggroGeneration,
                                 minAggroGeneration,
                                 maxAggroGeneration,
                                 initialAggro,
                                 minHealingPower,
                                 maxHealingPower,
                                 minHypnotizationPower,
                                 maxHypnotizationPower);
    }
}
