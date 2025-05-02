package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.movement.MovementResponse;

public interface GameUseCase {
    void startGame(String email, long seed, int size);
    MovementResponse move(String email, Vector2 positionToMove);
    
}
