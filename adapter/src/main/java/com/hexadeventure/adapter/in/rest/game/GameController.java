package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.StartGameDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.ChunkDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.movement.MovementResponseDTO;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.model.movement.MovementResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class GameController {
    
    private final GameUseCase gameUseCase;
    
    public GameController(GameUseCase gameUseCase) {
        this.gameUseCase = gameUseCase;
    }
    
    @PostMapping("/start")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid game size or seed"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game already started"),
    })
    public ResponseEntity<Void> startGame(Principal principal, @RequestBody StartGameDTO startGameDTO) {
        gameUseCase.startGame(principal.getName(), startGameDTO.seed(), startGameDTO.size());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/game/chunks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chunks data retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ChunkDataDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<ChunkDataDTO> getChunks(Principal principal) {
        ChunkDataDTO response = ChunkDataDTO.fromModel(gameUseCase.getChunks(principal.getName()));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/game/move")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movement successful",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = MovementResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid position",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started or in combat",
                         content = @Content),
    })
    public ResponseEntity<MovementResponseDTO> move(Principal principal, @RequestBody Vector2DTO position) {
        MovementResponse response = gameUseCase.move(principal.getName(), Vector2DTO.toModel(position));
        return ResponseEntity.ok(MovementResponseDTO.fromModel(response));
    }
    
    @PostMapping("/finish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game finished successfully"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started"),
    })
    public ResponseEntity<Void> finishGame(Principal principal) {
        gameUseCase.finishGame(principal.getName());
        return ResponseEntity.ok().build();
    }
}
