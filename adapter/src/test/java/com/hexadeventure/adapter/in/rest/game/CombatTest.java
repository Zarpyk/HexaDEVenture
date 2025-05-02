package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.combat.CombatStatusDTO;
import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.model.combat.CombatTerrain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;

public class CombatTest {
    private static final int ROW_SIZE = 3;
    private static final int COLUMN_SIZE = 4;
    private static final CombatTerrain COMBAT_TERRAIN = new CombatTerrain(ROW_SIZE, COLUMN_SIZE);
    
    private static final int TEST_ROW = 0;
    private static final int TEST_COLUMN = 0;
    private static final String TEST_CHARACTER_ID = "testCharacterId";
    
    private final CombatUseCase combatUseCase = mock(CombatUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new CombatController(combatUseCase));
    }
    
    @Test
    public void givenNoStartGameUser_whenCheckCombatStatus_thenReturn405() {
        doThrow(GameNotStartedException.class).when(combatUseCase).getCombatStatus(UserFactory.EMAIL);
        RestCommon.get("/game/combat", true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenUserWithNoCombat_whenCheckCombatStatus_thenReturn405() {
        doThrow(CombatNotStartedException.class).when(combatUseCase).getCombatStatus(UserFactory.EMAIL);
        RestCommon.get("/game/combat", true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenUserWithCombat_whenCheckCombatStatus_thenReturnCombatStatusDTO() {
        when(combatUseCase.getCombatStatus(UserFactory.EMAIL)).thenReturn(COMBAT_TERRAIN);
        RestCommon.get("/game/combat", true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(CombatStatusDTO.class);
    }
    
    @Test
    public void givenPositionWithCharacterIDOnEmptyPosition_whenPlaceCharacter_thenReturnOK() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenNonEmptyPosition_whenPlaceCharacter_thenReturn405() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        doThrow(PositionOccupiedException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                    TEST_ROW,
                                                                                    TEST_COLUMN,
                                                                                    TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenNonExistingCharacter_whenPlaceCharacter_thenReturn405() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        doThrow(CharacterNotFoundException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                     TEST_ROW,
                                                                                     TEST_COLUMN,
                                                                                     TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenInvalidPosition_whenPlaceCharacter_thenReturn400() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(-1, -1, TEST_CHARACTER_ID);
        doThrow(InvalidPositionException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                   -1,
                                                                                   -1,
                                                                                   TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenNonEmptyPosition_whenRemoveCharacter_thenReturnOK() {
        RemoveCharacterDTO removeCharacterDTO = new RemoveCharacterDTO(TEST_ROW, TEST_COLUMN);
        RestCommon.deleteWithBody("/game/combat/character", removeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenPositionOnEmptyPosition_whenRemoveCharacter_thenReturn405() {
        RemoveCharacterDTO removeCharacterDTO = new RemoveCharacterDTO(TEST_ROW, TEST_COLUMN);
        doThrow(PositionOccupiedException.class).when(combatUseCase).removeCharacter(UserFactory.EMAIL,
                                                                                     TEST_ROW,
                                                                                     TEST_COLUMN);
        RestCommon.deleteWithBody("/game/combat/character", removeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenInvalidPosition_whenRemoveCharacter_thenReturn400() {
        RemoveCharacterDTO removeCharacterDTO = new RemoveCharacterDTO(-1, -1);
        doThrow(InvalidPositionException.class).when(combatUseCase).removeCharacter(UserFactory.EMAIL,
                                                                                   -1,
                                                                                   -1);
        RestCommon.deleteWithBody("/game/combat/character", removeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
