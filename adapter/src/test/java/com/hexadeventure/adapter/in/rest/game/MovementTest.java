package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.movement.MovementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MovementTest {
    private static final Vector2 movePosition = new Vector2(10, 10);
    private static final MovementResponse MOVEMENT_RESPONSE = new MovementResponse(new ArrayList<>());
    
    private final GameUseCase gameUseCase = mock(GameUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
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
        when(gameUseCase.move(UserFactory.EMAIL, movePosition)).thenReturn(MOVEMENT_RESPONSE);
        MovementDTO body = new MovementDTO(movePosition.x, movePosition.y);
        RestCommon.postWithBody("/game/move", body, true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(MovementResponse.class);
    }
}
