package com.hexadeventure.model.combat;

import java.util.List;

public record CombatProcess(List<TurnInfo> turns, boolean combatFinished, boolean isBossBattle, boolean lose) {
}
