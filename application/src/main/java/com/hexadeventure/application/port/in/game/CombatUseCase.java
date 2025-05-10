package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;

public interface CombatUseCase {
    CombatTerrain getCombatStatus(String email);
    void placeCharacter(String email, int row, int column, String characterId);
    void removeCharacter(String email, int row, int column);
    CombatProcess processCombatTurn(String email);
}
