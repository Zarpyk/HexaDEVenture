package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.movement.MovementResponseDTO;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.model.movement.MovementResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @PostMapping("/game/move")
    public ResponseEntity<MovementResponseDTO> move(Principal principal, @RequestBody MovementDTO movementDTO) {
        MovementResponse response = gameUseCase.move(principal.getName(), movementDTO.toModel());
        return ResponseEntity.ok(MovementResponseDTO.fromModel(response));
    }
}
