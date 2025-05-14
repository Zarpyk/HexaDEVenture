package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;

public record CharacterDTO(String id,
                           String name,
                           ItemDTO weapon) {
    public static CharacterDTO fromModel(PlayableCharacter model) {
        return new CharacterDTO(model.getId(),
                                model.getName(),
                                ItemDTO.fromModel(model.getWeapon()));
    }
}
