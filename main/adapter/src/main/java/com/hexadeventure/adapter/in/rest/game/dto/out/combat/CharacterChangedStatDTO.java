package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.inventory.characters.ChangedStats;

public record CharacterChangedStatDTO(double health,
                                      boolean hypnotized,
                                      double boostHealth,
                                      double boostSpeed,
                                      double boostStrength,
                                      double boostDefense) {
    public static CharacterChangedStatDTO fromModel(ChangedStats model) {
        return new CharacterChangedStatDTO(model.getHealth(),
                                           model.isHypnotized(),
                                           model.getBoostHealth(),
                                           model.getBoostSpeed(),
                                           model.getBoostStrength(),
                                           model.getBoostDefense());
    }
}
