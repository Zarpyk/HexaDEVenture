package com.hexadeventure.adapter.in.rest.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.hexadeventure.application.port.in.game.GameUseCase;

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
}
