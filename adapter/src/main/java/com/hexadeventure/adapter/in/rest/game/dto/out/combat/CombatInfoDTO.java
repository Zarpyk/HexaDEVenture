package com.hexadeventure.adapter.in.rest.game.dto.out.combat;

import com.hexadeventure.model.combat.CombatTerrain;

import java.util.Arrays;

public record CombatInfoDTO(CharacterDataDTO[][] playerCharacters,
                            CharacterDataDTO[][] enemies) {
    public static CombatInfoDTO fromModel(CombatTerrain model) {
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
        return new CombatInfoDTO(playerArray, enemiesArray);
    }
}
