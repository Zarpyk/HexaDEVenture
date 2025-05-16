package com.hexadeventure.adapter.out.settings.json.enemy;

import com.hexadeventure.model.inventory.characters.EnemySetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnemyJson {
    private String id;
    private WeaponType weaponType;
    private int minHealth;
    private int maxHealth;
    private int minSpeed;
    private int maxSpeed;
    private int minHypnotizationResistance;
    private int maxHypnotizationResistance;
    
    public EnemySetting toModel() {
        return new EnemySetting(
                id,
                weaponType,
                minHealth,
                maxHealth,
                minSpeed,
                maxSpeed,
                minHypnotizationResistance,
                maxHypnotizationResistance
        );
    }
}
