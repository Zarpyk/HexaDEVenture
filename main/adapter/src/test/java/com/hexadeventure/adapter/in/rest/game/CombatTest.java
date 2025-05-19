package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.in.PlaceCharacterDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatInfoDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatProcessDTO;
import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.model.combat.CombatAction;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.combat.TurnInfo;
import com.hexadeventure.model.inventory.characters.CharacterStat;
import com.hexadeventure.model.inventory.characters.CharacterStatusChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class CombatTest {
    private static final int TEST_ROW = 0;
    private static final int TEST_COLUMN = 0;
    private static final String TEST_CHARACTER_ID = "testCharacterId";
    
    private static final int ROW_SIZE = 3;
    private static final int COLUMN_SIZE = 4;
    private static final CombatTerrain COMBAT_TERRAIN = new CombatTerrain(ROW_SIZE, COLUMN_SIZE);
    
    private static final List<CharacterStatusChange> STATUS_CHANGES =
            new ArrayList<>(Collections.singleton(new CharacterStatusChange(
                    CharacterStat.HEALTH,
                    10,
                    0)));
    
    private static final CombatProcess COMBAT_INFO =
            new CombatProcess(new ArrayList<>(Collections.singleton(new TurnInfo(
                    CombatAction.ATTACK,
                    false,
                    0,
                    0,
                    STATUS_CHANGES,
                    0,
                    0,
                    STATUS_CHANGES))), false, false, false);
    
    
    private final CombatUseCase combatUseCase = mock(CombatUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new CombatController(combatUseCase));
    }
    
    //region CheckCombatStatus
    @Test
    public void givenNoStartGameUser_whenCheckCombatStatus_thenReturnMethodNotAllowed() {
        doThrow(GameNotStartedException.class).when(combatUseCase).getCombatStatus(UserFactory.EMAIL);
        RestCommon.get("/game/combat", true).then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenUserWithNoCombat_whenCheckCombatStatus_thenReturnMethodNotAllowed() {
        doThrow(CombatNotStartedException.class).when(combatUseCase).getCombatStatus(UserFactory.EMAIL);
        RestCommon.get("/game/combat", true).then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenUserWithCombat_whenCheckCombatStatus_thenReturnOnWithDTO() {
        when(combatUseCase.getCombatStatus(UserFactory.EMAIL)).thenReturn(COMBAT_TERRAIN);
        RestCommon.get("/game/combat", true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract()
                  .body()
                  .as(CombatInfoDTO.class);
    }
    //endregion
    
    //region PlaceCharacter
    @Test
    public void givenPositionWithCharacterIDOnEmptyPosition_whenPlaceCharacter_thenReturnOK() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenNonEmptyPosition_whenPlaceCharacter_thenReturnMethodNotAllowed() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        doThrow(InvalidPositionException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                   TEST_ROW,
                                                                                   TEST_COLUMN,
                                                                                   TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenNonExistingCharacter_whenPlaceCharacter_thenReturnMethodNotAllowed() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(TEST_ROW, TEST_COLUMN, TEST_CHARACTER_ID);
        doThrow(InvalidCharacterException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                    TEST_ROW,
                                                                                    TEST_COLUMN,
                                                                                    TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenInvalidPosition_whenPlaceCharacter_thenReturnBadRequest() {
        PlaceCharacterDTO placeCharacterDTO = new PlaceCharacterDTO(-1, -1, TEST_CHARACTER_ID);
        doThrow(InvalidPositionException.class).when(combatUseCase).placeCharacter(UserFactory.EMAIL,
                                                                                   -1,
                                                                                   -1,
                                                                                   TEST_CHARACTER_ID);
        RestCommon.postWithBody("/game/combat/character", placeCharacterDTO, true)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    //endregion
    
    //region RemoveCharacter
    @Test
    public void givenNonEmptyPosition_whenRemoveCharacter_thenReturnOK() {
        RestCommon.deleteWithParam("/game/combat/character",
                                   "row",
                                   Integer.toString(TEST_ROW),
                                   "column",
                                   Integer.toString(TEST_ROW))
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenPositionOnEmptyPosition_whenRemoveCharacter_thenReturnMethodNotAllowed() {
        doThrow(InvalidPositionException.class).when(combatUseCase).removeCharacter(UserFactory.EMAIL,
                                                                                    TEST_ROW,
                                                                                    TEST_COLUMN);
        RestCommon.deleteWithParam("/game/combat/character",
                                   "row",
                                   Integer.toString(TEST_ROW),
                                   "column",
                                   Integer.toString(TEST_ROW))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenInvalidPosition_whenRemoveCharacter_thenReturnBadRequest() {
        doThrow(InvalidPositionException.class).when(combatUseCase).removeCharacter(UserFactory.EMAIL, -1, -1);
        RestCommon.deleteWithParam("/game/combat/character",
                                   "row",
                                   Integer.toString(-1),
                                   "column",
                                   Integer.toString(-1))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    //endregion
    
    //region ProcessCombatTurn
    @Test
    public void givenStartCombatUser_whenProcessCombatTurn_thenOkWithDTO() {
        when(combatUseCase.processCombatTurn(UserFactory.EMAIL)).thenReturn(COMBAT_INFO);
        RestCommon.post("/game/combat/process", true)
                  .then().statusCode(HttpStatus.OK.value())
                  .extract().body().as(CombatProcessDTO.class);
    }
    
    @Test
    public void givenNoCharacterTerrain_whenProcessCombatTurn_thenReturnBadRequest() {
        doThrow(NoCharacterOnTerrainException.class).when(combatUseCase).processCombatTurn(UserFactory.EMAIL);
        RestCommon.post("/game/combat/process", true)
                  .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    //endregion
}
