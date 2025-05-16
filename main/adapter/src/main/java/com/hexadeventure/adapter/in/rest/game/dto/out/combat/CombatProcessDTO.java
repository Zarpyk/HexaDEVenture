package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.combat.CombatProcess;

import java.util.List;

public record CombatProcessDTO(List<TurnInfoDTO> turns,
                               boolean combatFinished,
                               boolean isBossBattle,
                               boolean lose) {
    public static CombatProcessDTO fromModel(CombatProcess combatProcess) {
        return new CombatProcessDTO(combatProcess.turns().stream()
                                                 .map(TurnInfoDTO::fromModel)
                                                 .toList(),
                                    combatProcess.combatFinished(),
                                    combatProcess.isBossBattle(),
                                    combatProcess.lose());
    }
}
