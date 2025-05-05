package com.hexadeventure.model.combat;

import com.hexadeventure.model.inventory.characters.CharacterStatusChange;

import java.util.List;

public record TurnInfo(CombatAction action, boolean isEnemyTurn,
                       int row, int column, List<CharacterStatusChange> characterStatus,
                       int targetRow, int targetColumn, List<CharacterStatusChange> targetStatus) {
}
