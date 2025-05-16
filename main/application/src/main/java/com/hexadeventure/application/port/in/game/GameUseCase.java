package com.hexadeventure.application.port.in.game;

import com.hexadeventure.model.map.ChunkData;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.movement.MovementResponse;

public interface GameUseCase {
    void startGame(String email, long seed, int size);
    ChunkData getChunks(String email);
    MovementResponse move(String email, Vector2 positionToMove);
    void finishGame(String email);
}
