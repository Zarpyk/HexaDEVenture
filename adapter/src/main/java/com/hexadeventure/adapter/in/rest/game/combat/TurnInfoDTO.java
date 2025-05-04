package com.hexadeventure.adapter.in.rest.game.combat;

import com.hexadeventure.model.combat.CombatAction;
import com.hexadeventure.model.combat.TurnInfo;

public record TurnInfoDTO(CombatAction action,
                          int row, int column, CharacterDataDTO characterStatus,
                          int targetRow, int targetColumn, CharacterDataDTO targetStatus) {
    public static TurnInfoDTO fromModel(TurnInfo model) {
        return new TurnInfoDTO(model.action(),
                               model.row(),
                               model.column(),
                               CharacterDataDTO.fromModel(model.characterStatus()),
                               model.targetRow(),
                               model.targetColumn(),
                               CharacterDataDTO.fromModel(model.targetStatus()));
    }
}
