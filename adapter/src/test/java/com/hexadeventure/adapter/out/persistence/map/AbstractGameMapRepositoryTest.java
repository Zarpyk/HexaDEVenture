package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractGameMapRepositoryTest {
    private static final String TEST_USER_ID = UUID.randomUUID().toString();
    private static final long TEST_SEED = 12345L;
    private static final int TEST_SIZE = 100;
    
    @Autowired
    public GameMapRepository repo;
    
    private static final Random random = new Random();
    private static GameMap testMap;
    
    @BeforeAll
    public static void beforeAll() {
        testMap = new GameMap(TEST_USER_ID, TEST_SEED, TEST_SIZE);
        
        for (int x = 0; x < TEST_SIZE; x++) {
            for (int y = 0; y < TEST_SIZE; y++) {
                // Exclusive 1 but it is never used precisely on the creation of the cell
                double threshold = random.nextDouble();
                testMap.createCell(threshold, x, y);
            }
        }
        
        testMap.initMainCharacter(new Vector2(TEST_SIZE / 2, TEST_SIZE / 2));
    }
    
    @AfterEach
    public void afterEach() {
        repo.deleteById(testMap.getId());
    }
    
    @Test
    public void givenId_whenFind_thenReturnTheGameMap() {
        repo.save(testMap);
        
        Optional<GameMap> map = repo.findById(testMap.getId());
        assertThat(map).isPresent();
        assertThat(map.get()).isEqualTo(testMap);
    }
    
    @Test
    public void givenNonExistingId_whenFind_thenReturnEmptyOptional() {
        Optional<GameMap> map = repo.findById("non-existing-id");
        
        assertThat(map).isEmpty();
    }
    
    @Test
    public void givenGameMap_whenSave_thenGameMapIsSaved() {
        repo.save(testMap);
        
        Optional<GameMap> map = repo.findById(testMap.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getId()).isEqualTo(testMap.getId());
    }
    
    @Test
    public void givenExistingMap_whenSave_thenMapIsUpdated() {
        repo.save(testMap);
        
        Vector2 newPos = new Vector2(10, 10);
        GameMap map = repo.findById(testMap.getId()).orElse(null);
        assertThat(map).isNotNull();
        assertThat(map.getMainCharacter().getPosition()).isNotEqualTo(newPos);
        
        testMap.initMainCharacter(newPos);
        repo.save(testMap);
        
        Optional<GameMap> updatedMap = repo.findById(testMap.getId());
        assertThat(updatedMap).isPresent();
        GameMap mapObject = updatedMap.get();
        assertThat(mapObject.getMainCharacter().getPosition()).isEqualTo(newPos);
    }
    
    @Test
    public void givenExistingMap_whenDeleteById_thenMapIsDeleted() {
        repo.save(testMap);
        
        repo.deleteById(testMap.getId());
        
        Optional<GameMap> result = repo.findById(testMap.getId());
        assertThat(result).isEmpty();
    }
}