package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponType;

public record WeaponDataDTO(WeaponType weaponType,
                            double damage,
                            double meleeDefense,
                            double rangedDefense,
                            int cooldown,
                            double aggroGeneration,
                            int initialAggro,
                            double healingPower,
                            double hypnotizationPower) {
    public static WeaponDataDTO fromModel(Weapon model) {
        if (model == null) return null;
        return new WeaponDataDTO(model.getWeaponType(),
                                 model.getDamage(),
                                 model.getMeleeDefense(),
                                 model.getRangedDefense(),
                                 model.getCooldown(),
                                 model.getAggroGeneration(),
                                 model.getInitialAggro(),
                                 model.getHealingPower(),
                                 model.getHypnotizationPower());
    }
}
