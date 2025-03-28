package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.model.map.MainCharacter;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.map.*;

import java.util.Map;
import java.util.UUID;

public class MapFactory {
    public static final String TEST_USER_EMAIL = "test@test.com";
    public static final long TEST_SEED = 12345L;
    public static final int TEST_SIZE = Chunk.SIZE * 4;
    
    public static final Chunk CHUNK1 = new Chunk(new Vector2C(0, 0));
    public static final Chunk CHUNK2 = new Chunk(new Vector2C(1, 0));
    public static final Chunk CHUNK3 = new Chunk(new Vector2C(0, 1));
    public static final Chunk CHUNK4 = new Chunk(new Vector2C(1, 1));
    
    public static final GameMap GAME_MAP;
    public static final GameMap GAME_MAP_WITH_2_CHUNKS;
    public static final GameMap GAME_MAP_WITH_CHUNKS;
    
    static {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                CHUNK1.setCell(new Vector2(x, y), CellType.GROUND);
                CHUNK2.setCell(new Vector2(x, y), CellType.GROUND);
                CHUNK3.setCell(new Vector2(x, y), CellType.GROUND);
                CHUNK4.setCell(new Vector2(x, y), CellType.GROUND);
            }
        }
        
        GAME_MAP = new GameMap(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        
        Map<Vector2C, Chunk> chunks = Map.of(
                CHUNK1.getPosition(), CHUNK1,
                CHUNK2.getPosition(), CHUNK2
        );
        GAME_MAP_WITH_2_CHUNKS = new GameMap(UUID.randomUUID().toString(),
                                             TEST_USER_EMAIL,
                                             TEST_SEED,
                                             TEST_SIZE,
                                             chunks,
                                             new MainCharacter(new Vector2(TEST_SIZE / 2, TEST_SIZE / 2)),
                                             new Inventory());
        
        chunks = Map.of(
                CHUNK1.getPosition(), CHUNK1,
                CHUNK2.getPosition(), CHUNK2,
                CHUNK3.getPosition(), CHUNK3,
                CHUNK4.getPosition(), CHUNK4
        );
        GAME_MAP_WITH_CHUNKS = new GameMap(UUID.randomUUID().toString(),
                                           TEST_USER_EMAIL,
                                           TEST_SEED,
                                           TEST_SIZE,
                                           chunks,
                                           new MainCharacter(new Vector2(TEST_SIZE / 2, TEST_SIZE / 2)),
                                           new Inventory());
    }
}
