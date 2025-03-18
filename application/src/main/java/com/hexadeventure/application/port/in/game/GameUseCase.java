package com.hexadeventure.application.port.in.game;

public interface GameUseCase {
    void startGame(String email, long seed, int size);
}
