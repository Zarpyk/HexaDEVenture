package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.service.game.MovementResponseDTO;
import com.hexadeventure.model.map.Vector2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class MovementTest {
    private static final Vector2 movePosition = new Vector2(10, 10);
    private static final MovementResponseDTO movementResponseDTO = new MovementResponseDTO(new ArrayList<>());
    
    @Mock
    private GameUseCase gameUseCase;
    
    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestCommon.Setup(new GameController(gameUseCase));
    }
    
    @Test
    public void givenNoStartGameUser_whenMove_thenReturn405() {
        doThrow(GameNotStartedException.class).when(gameUseCase).move(UserFactory.EMAIL, movePosition);
        MovementDTO body = new MovementDTO(movePosition.x, movePosition.y);
        RestCommon.postWithBody("/game/move", body, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenPosition_whenMove_thenReturnMovementDTO() {
        when(gameUseCase.move(UserFactory.EMAIL, movePosition)).thenReturn(movementResponseDTO);
        MovementDTO body = new MovementDTO(movePosition.x, movePosition.y);
        RestCommon.postWithBody("/game/move", body, true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(MovementResponseDTO.class);
    }
}
