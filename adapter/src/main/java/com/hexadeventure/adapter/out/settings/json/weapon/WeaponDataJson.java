package com.hexadeventure.adapter.out.settings.json.weapon;

import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.model.inventory.weapons.AggroGenType;
import com.hexadeventure.model.inventory.weapons.WeaponData;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeaponDataJson extends ItemJson<WeaponData> {
    private WeaponJsonType weaponType;
    
    private double minDamage;
    private double maxDamage;
    
    private double minMeleeDefense;
    private double maxMeleeDefense;
    
    private double minRangedDefense;
    private double maxRangedDefense;
    
    private int minCooldown;
    private int maxCooldown;
    
    private AggroGenJsonType aggroGenType;
    private double extraAggroGeneration;
    private double minAggroGeneration;
    private double maxAggroGeneration;
    
    private int initialAggro;
    private double healingPower;
    private double hipnotizationPower;
    
    public static String getID(WeaponDataJson weaponDataJson) {
        return weaponDataJson.getName();
    }
    
    public WeaponData toModel() {
        return new WeaponData(getName(),
                              getSkin(),
                              WeaponType.values()[weaponType.ordinal()],
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
                              healingPower,
                              hipnotizationPower);
    }
}
