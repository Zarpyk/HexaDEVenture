package com.hexadeventure.adapter.out.settings.json.enemy;

import com.hexadeventure.adapter.out.settings.json.weapon.WeaponJsonType;
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
    private WeaponJsonType weaponType;
    private int minHealth;
    private int maxHealth;
    private int minSpeed;
    private int maxSpeed;
    private int minHypnotizationResistence;
    private int maxHypnotizationResistence;
    
    public EnemySetting toModel() {
        return new EnemySetting(
                id,
                WeaponType.values()[weaponType.ordinal()],
                minHealth,
                maxHealth,
                minSpeed,
                maxSpeed,
                minHypnotizationResistence,
                maxHypnotizationResistence
        );
    }
}
