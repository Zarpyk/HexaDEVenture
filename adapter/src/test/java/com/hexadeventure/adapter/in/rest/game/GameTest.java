package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.in.StartGameDTO;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class GameTest {
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = 100;
    
    private final GameUseCase gameUseCase = mock(GameUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new GameController(gameUseCase));
    }
    
    //region StartGame
    @Test
    public void givenSeedAndSize_whenStartingGameWithoutStartedGame_thenReturnCreated() {
        StartGameDTO body = new StartGameDTO(TEST_SEED, TEST_SIZE);
        RestCommon.postWithBody("/start", body, true)
                  .then()
                  .statusCode(HttpStatus.CREATED.value());
    }
    
    @Test
    public void givenSeedAndSize_whenStartingGameWithStartedGame_thenReturnMethodNotAllowed() {
        doThrow(new GameStartedException()).when(gameUseCase).startGame(UserFactory.EMAIL, TEST_SEED, TEST_SIZE);
        
        StartGameDTO body = new StartGameDTO(TEST_SEED, TEST_SIZE);
        
        RestCommon.postWithBody("/start", body, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    //endregion
    
    //region FinishGame
    @Test
    public void givenStartedGame_whenFinishGame_thenReturnOk() {
        RestCommon.post("/finish", true)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenNoStartedGame_whenFinishGame_thenReturnMethodNotAllowed() {
        doThrow(new GameNotStartedException()).when(gameUseCase).finishGame(UserFactory.EMAIL);
        
        RestCommon.post("/finish", true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    //endregion
}
