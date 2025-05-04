package com.hexadeventure.model.combat;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;

public record TurnInfo(CombatAction action,
                       int row, int column, PlayableCharacter characterStatus,
                       int targetRow, int targetColumn, PlayableCharacter targetStatus) {
}
