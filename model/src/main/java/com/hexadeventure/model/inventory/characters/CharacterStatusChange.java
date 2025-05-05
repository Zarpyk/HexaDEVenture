package com.hexadeventure.model.inventory.characters;

public record CharacterStatusChange(CharacterStat statChanged,
                                    double oldValue,
                                    double newValue) {
}
