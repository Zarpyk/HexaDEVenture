package com.hexadeventure.application.port.in.game;

import com.hexadeventure.application.service.game.MovementResponseDTO;
import com.hexadeventure.model.map.Vector2;

public interface GameUseCase {
    void startGame(String email, long seed, int size);
    MovementResponseDTO move(String email, Vector2 positionToMove);
}
