package com.hexadeventure.adapter.in.rest.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.ChunkDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.movement.MovementResponseDTO;
import com.hexadeventure.adapter.utils.Vector2DTODeserializer;
import com.hexadeventure.adapter.utils.Vector2Deserializer;
import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.ChunkData;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import com.hexadeventure.model.movement.MovementResponse;
import io.restassured.internal.mapping.Jackson2Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MovementTest {
    private static final Vector2DTO MOVE_POSITION = new Vector2DTO(10, 10);
    private static final MovementResponse MOVEMENT_RESPONSE = new MovementResponse(new ArrayList<>());
    private static final ChunkData CHUNK_DATA = new ChunkData(Map.of(new Vector2C(0, 0),
                                                                     new Chunk(new Vector2C(0, 0))));
    
    private final GameUseCase gameUseCase = mock(GameUseCase.class);
    
    // From: https://stackoverflow.com/a/25070542/11451105
    private static final Jackson2Mapper objectMapper = new Jackson2Mapper(
            (_, _) -> {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addKeyDeserializer(Vector2.class, new Vector2Deserializer());
                simpleModule.addKeyDeserializer(Vector2DTO.class, new Vector2DTODeserializer());
                objectMapper.registerModule(simpleModule);
                return objectMapper;
            }
    );
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new GameController(gameUseCase));
    }
    
    @Test
    public void givenNoStartGameUser_whenRequestChunks_thenReturnMethodNotAllowed() {
        doThrow(GameNotStartedException.class).when(gameUseCase).getChunks(UserFactory.EMAIL);
        RestCommon.get("/game/chunks", true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenNothing_whenRequestChunks_thenReturnOkWithDTO() {
        when(gameUseCase.getChunks(UserFactory.EMAIL)).thenReturn(CHUNK_DATA);
        RestCommon.get("/game/chunks", true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(ChunkDataDTO.class, objectMapper);
    }
    
    @Test
    public void givenNoStartGameUser_whenMove_thenReturnMethodNotAllowed() {
        doThrow(GameNotStartedException.class).when(gameUseCase).move(UserFactory.EMAIL,
                                                                      Vector2DTO.toModel(MOVE_POSITION));
        RestCommon.postWithBody("/game/move", MOVE_POSITION, true)
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenPosition_whenMove_thenOkWithDTO() {
        when(gameUseCase.move(UserFactory.EMAIL, Vector2DTO.toModel(MOVE_POSITION))).thenReturn(MOVEMENT_RESPONSE);
        RestCommon.postWithBody("/game/move", MOVE_POSITION, true)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(MovementResponseDTO.class);
    }
}
