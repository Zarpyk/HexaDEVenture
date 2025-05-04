package com.hexadeventure.adapter.in.rest.game.combat;

import com.hexadeventure.model.combat.CombatProcess;

import java.util.List;

public record CombatProcessDTO(List<TurnInfoDTO> turns) {
    public static CombatProcessDTO fromModel(CombatProcess combatProcess) {
        return new CombatProcessDTO(combatProcess.turns().stream()
                                                 .map(TurnInfoDTO::fromModel)
                                                 .toList());
    }
}
