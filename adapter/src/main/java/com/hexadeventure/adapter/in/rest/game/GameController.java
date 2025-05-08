package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.StartGameDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.ChunkDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.movement.MovementResponseDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.model.movement.MovementResponse;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> startGame(Principal principal, @RequestBody StartGameDTO startGameDTO) {
        gameUseCase.startGame(principal.getName(), startGameDTO.seed(), startGameDTO.size());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/game/chunks")
    public ResponseEntity<ChunkDataDTO> getChunks(Principal principal) {
        ChunkDataDTO response = ChunkDataDTO.fromModel(gameUseCase.getChunks(principal.getName()));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/game/move")
    public ResponseEntity<MovementResponseDTO> move(Principal principal, @RequestBody Vector2DTO position) {
        MovementResponse response = gameUseCase.move(principal.getName(), Vector2DTO.toModel(position));
        return ResponseEntity.ok(MovementResponseDTO.fromModel(response));
    }
}
