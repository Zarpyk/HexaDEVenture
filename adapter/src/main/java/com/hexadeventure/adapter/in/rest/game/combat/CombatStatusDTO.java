package com.hexadeventure.adapter.in.rest.game.combat;

import com.hexadeventure.model.combat.CombatTerrain;

import java.util.Arrays;

public record CombatStatusDTO(CharacterDataDTO[][] playerCharacters,
                              CharacterDataDTO[][] enemies) {
    public static CombatStatusDTO fromModel(CombatTerrain model) {
        CharacterDataDTO[][] playerArray = Arrays.stream(model.getPlayerTerrain())
                                                 .map(array -> Arrays.stream(array)
                                                                     .map(CharacterDataDTO::fromModel)
                                                                     .toArray(CharacterDataDTO[]::new))
                                                 .toArray(CharacterDataDTO[][]::new);
        CharacterDataDTO[][] enemiesArray = Arrays.stream(model.getEnemyTerrain())
                                                  .map(array -> Arrays.stream(array)
                                                                      .map(CharacterDataDTO::fromModel)
                                                                      .toArray(CharacterDataDTO[]::new))
                                                  .toArray(CharacterDataDTO[][]::new);
        return new CombatStatusDTO(playerArray, enemiesArray);
    }
}
