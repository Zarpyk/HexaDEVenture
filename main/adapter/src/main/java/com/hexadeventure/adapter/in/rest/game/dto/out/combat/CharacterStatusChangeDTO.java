package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.inventory.characters.CharacterStat;
import com.hexadeventure.model.inventory.characters.CharacterStatusChange;

public record CharacterStatusChangeDTO(CharacterStat statChanged,
                                       double oldValue,
                                       double newValue) {
    public static CharacterStatusChangeDTO fromModel(CharacterStatusChange model) {
        return new CharacterStatusChangeDTO(model.statChanged(),
                                            model.oldValue(),
                                            model.newValue());
    }
}
