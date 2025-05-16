package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;

public record CharacterDataDTO(String id,
                               String name,
                               double health,
                               double speed,
                               WeaponDataDTO weapon,
                               CharacterChangedStatDTO changedStats) {
    public static CharacterDataDTO fromModel(PlayableCharacter model) {
        if(model == null) return null;
        return new CharacterDataDTO(model.getId(),
                                    model.getName(),
                                    model.getHealth(),
                                    model.getSpeed(),
                                    WeaponDataDTO.fromModel(model.getWeapon()),
                                    CharacterChangedStatDTO.fromModel(model.getChangedStats()));
    }
}
