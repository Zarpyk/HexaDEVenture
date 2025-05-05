package com.hexadeventure.adapter.in.rest.game.combat;

import com.hexadeventure.model.combat.CombatAction;
import com.hexadeventure.model.combat.TurnInfo;

import java.util.List;

public record TurnInfoDTO(CombatAction action, boolean isEnemyTurn,
                          int row, int column, List<CharacterStatusChangeDTO> characterStatus,
                          int targetRow, int targetColumn, List<CharacterStatusChangeDTO> targetStatus) {
    public static TurnInfoDTO fromModel(TurnInfo model) {
        return new TurnInfoDTO(model.action(),
                               model.isEnemyTurn(),
                               model.row(),
                               model.column(),
                               model.characterStatus().stream()
                                    .map(CharacterStatusChangeDTO::fromModel)
                                    .toList(),
                               model.targetRow(),
                               model.targetColumn(),
                               model.targetStatus().stream()
                                    .map(CharacterStatusChangeDTO::fromModel)
                                    .toList());
    }
}
