package com.hexadeventure.common;

import com.hexadeventure.model.map.GameMap;

public class GameMapFactory {
    public final static long SEED = 1234;
    public final static int SIZE = 10;
    
    public static GameMap createGameMap() {
        return new GameMap(UserFactory.EMAIL, SEED, SIZE);
    }
}
