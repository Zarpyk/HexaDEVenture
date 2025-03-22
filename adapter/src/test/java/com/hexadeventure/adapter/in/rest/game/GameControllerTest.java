package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;


@RunWith(SpringRunner.class)
public class GameControllerTest {
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = 100;
    
    @Mock
    private GameUseCase gameUseCase;
    
    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestCommon.Setup(new GameController(gameUseCase));
    }
    
    @Test
    public void givenSeedAndSize_whenStartingGameWithoutStartedGame_thenReturn201() {
        doNothing().when(gameUseCase).startGame(UserFactory.EMAIL, TEST_SEED, TEST_SIZE);
        StartGameDTO body = new StartGameDTO(TEST_SEED, TEST_SIZE);
        RestCommon.postWithBody("/start", body, true)
                  .then()
                  .statusCode(HttpStatus.CREATED.value());
    }
    
    @Test
    public void givenSeedAndSize_whenStartingGameWithStartedGame_thenReturn405() {
        doThrow(new GameStartedException()).when(gameUseCase).startGame(UserFactory.EMAIL, TEST_SEED, TEST_SIZE);
        
        StartGameDTO body = new StartGameDTO(TEST_SEED, TEST_SIZE);
        
        RestCommon.postWithBody("/start", body, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
}
