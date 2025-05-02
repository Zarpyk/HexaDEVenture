package com.hexadeventure.adapter.in.rest.game.combat;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;

public record CharacterDataDTO(String id,
                               String name,
                               int health,
                               int speed,
                               WeaponDataDTO weapon) {
    public static CharacterDataDTO fromModel(PlayableCharacter model) {
        if(model == null) return null;
        return new CharacterDataDTO(model.getId(),
                                    model.getName(),
                                    model.getHealth(),
                                    model.getSpeed(),
                                    WeaponDataDTO.fromModel(model.getWeapon()));
    }
}
