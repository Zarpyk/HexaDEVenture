package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SplittableRandom;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractGameMapRepositoryTest {
    @Autowired
    public GameMapRepository gameMapRepository;
    @Autowired
    public ChunkRepository chunkRepository;
    
    @AfterEach
    public void afterEach() {
        gameMapRepository.deleteById(MapFactory.GAME_MAP.getId());
        chunkRepository.deleteByMapId(MapFactory.GAME_MAP.getId());
        gameMapRepository.deleteById(MapFactory.GAME_MAP_WITH_2_CHUNKS.getId());
        chunkRepository.deleteByMapId(MapFactory.GAME_MAP_WITH_2_CHUNKS.getId());
        gameMapRepository.deleteById(MapFactory.GAME_MAP_WITH_CHUNKS.getId());
        chunkRepository.deleteByMapId(MapFactory.GAME_MAP_WITH_CHUNKS.getId());
    }
    
    @Test
    public void givenId_whenFind_thenReturnTheGameMap() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> map = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getId()).isEqualTo(MapFactory.GAME_MAP.getId());
        assertThat(map.get().getUserEmail()).isEqualTo(MapFactory.GAME_MAP.getUserEmail());
        assertThat(map.get().getSeed()).isEqualTo(MapFactory.GAME_MAP.getSeed());
        assertThat(map.get().getSize()).isEqualTo(MapFactory.GAME_MAP.getSize());
        
        assertThat(map.get().getMainCharacter()).isEqualTo(MapFactory.GAME_MAP.getMainCharacter());
        assertThat(map.get().getInventory()).isEqualTo(MapFactory.GAME_MAP.getInventory());
        assertThat(map.get().getCombatTerrain()).isEqualTo(MapFactory.GAME_MAP.getCombatTerrain());
        
        assertThat(map.get().getBossPosition()).isEqualTo(MapFactory.GAME_MAP.getBossPosition());
        assertThat(map.get().isInCombat()).isEqualTo(MapFactory.GAME_MAP.isInCombat());
        assertThat(map.get().isBossBattle()).isEqualTo(MapFactory.GAME_MAP.isBossBattle());
    }
    
    @Test
    public void givenNonExistingId_whenFind_thenReturnEmptyOptional() {
        Optional<GameMap> map = gameMapRepository.findById("non-existing-id");
        
        assertThat(map).isEmpty();
    }
    
    @Test
    public void givenMapWithChunks_whenFind_thenReturnWithoutChunks() {
        gameMapRepository.save(MapFactory.GAME_MAP_WITH_CHUNKS);
        
        Optional<GameMap> map = gameMapRepository.findById(MapFactory.GAME_MAP_WITH_CHUNKS.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getChunks()).isEmpty();
    }
    
    @Test
    public void givenMapWithChunks_whenFindWithChunks_thenReturnWithChunks() {
        gameMapRepository.save(MapFactory.GAME_MAP_WITH_CHUNKS);
        
        Optional<GameMap> map = gameMapRepository.findByIdAndGetChunks(MapFactory.GAME_MAP_WITH_CHUNKS.getId(),
                                                                       List.of(MapFactory.CHUNK1.getPosition(),
                                                                               MapFactory.CHUNK2.getPosition(),
                                                                               MapFactory.CHUNK3.getPosition(),
                                                                               MapFactory.CHUNK4.getPosition()));
        assertThat(map).isPresent();
        assertThat(map.get().getChunks().size()).isEqualTo(4);
    }
    
    @Test
    public void givenMapWithSomeChunks_whenFindWithChunks_thenReturnFoundChunks() {
        gameMapRepository.save(MapFactory.GAME_MAP_WITH_2_CHUNKS);
        
        Optional<GameMap> map = gameMapRepository.findByIdAndGetChunks(MapFactory.GAME_MAP_WITH_2_CHUNKS.getId(),
                                                                       List.of(MapFactory.CHUNK1.getPosition(),
                                                                               MapFactory.CHUNK2.getPosition(),
                                                                               MapFactory.CHUNK3.getPosition(),
                                                                               MapFactory.CHUNK4.getPosition()));
        
        assertThat(map).isPresent();
        assertThat(map.get().getChunks().size()).isEqualTo(2);
    }
    
    @Test
    public void givenMapWithoutChunks_whenFindWithChunks_thenReturnWithoutChunks() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> map = gameMapRepository.findByIdAndGetChunks(MapFactory.GAME_MAP.getId(),
                                                                       List.of(MapFactory.CHUNK1.getPosition(),
                                                                               MapFactory.CHUNK2.getPosition(),
                                                                               MapFactory.CHUNK3.getPosition(),
                                                                               MapFactory.CHUNK4.getPosition()));
        
        assertThat(map).isPresent();
        assertThat(map.get().getChunks().size()).isEqualTo(0);
    }
    
    @Test
    public void givenGameMap_whenSave_thenGameMapIsSaved() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> map = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getId()).isEqualTo(MapFactory.GAME_MAP.getId());
    }
    
    @Test
    public void givenExistingMap_whenSave_thenMapIsUpdated() {
        GameMap gameMap = MapFactory.createGameMap();
        gameMapRepository.save(gameMap);
        
        // Check initial state
        Vector2 newPos = new Vector2(10, 10);
        GameMap map = gameMapRepository.findById(gameMap.getId()).orElse(null);
        assertThat(map).isNotNull();
        assertThat(map.getMainCharacter().getPosition()).isNotEqualTo(newPos);
        
        gameMap.getMainCharacter().setPosition(newPos);
        gameMap.getInventory().addItem(new Food("test", 10, 10));
        gameMap.getCombatTerrain().setModifiable(false);
        gameMapRepository.save(gameMap);
        
        // Check the updated state
        Optional<GameMap> updatedMap = gameMapRepository.findById(gameMap.getId());
        assertThat(updatedMap).isPresent();
        GameMap mapObject = updatedMap.get();
        assertThat(mapObject.getMainCharacter().getPosition()).isEqualTo(newPos);
        assertThat(mapObject.getInventory().getItems()).hasSize(1);
        assertThat(mapObject.getCombatTerrain().isModifiable()).isFalse();
    }
    
    @Test
    public void givenChunk_whenSaveMap_thenChunksAreSaved() {
        gameMapRepository.save(MapFactory.GAME_MAP_WITH_CHUNKS);
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP_WITH_CHUNKS.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        assertThat(chunks.size()).isEqualTo(4);
    }
    
    
    @Test
    public void givenExistChunk_whenSaveMap_thenChunksAreUpdated() {
        Map<Vector2C, Chunk> chunks = MapFactory.createChunks();
        GameMap gameMap = MapFactory.createGameMapWithChunks(chunks);
        gameMapRepository.save(gameMap);
        
        // Check initial state
        Optional<GameMap> findedMap = gameMapRepository.findByIdAndGetChunks(gameMap.getId(),
                                                                             chunks.keySet().stream().toList());
        assertThat(findedMap).isPresent();
        Map<Vector2C, Chunk> findChunks = findedMap.get().getChunks();
        assertThat(findChunks.size()).isEqualTo(4);
        
        // Modify
        gameMap.addEnemy(new Vector2(0, 0), new Enemy());
        gameMap.addResource(new Vector2(0, 0), 10, new SplittableRandom());
        gameMapRepository.save(gameMap);
        
        // Check the updated state
        findedMap = gameMapRepository.findByIdAndGetChunks(gameMap.getId(),
                                                           chunks.keySet().stream().toList());
        assertThat(findedMap).isPresent();
        findChunks = findedMap.get().getChunks();
        
        assertThat(findChunks.size()).isEqualTo(4);
        Chunk chunk = findChunks.get(new Vector2C(0,0));
        assertThat(chunk).isNotNull();
        assertThat(chunk.getEnemies().size()).isEqualTo(1);
        assertThat(chunk.getResources().size()).isEqualTo(1);
        
        // Modify 2
        gameMap.moveEnemy(new Vector2(0, 0), new Vector2(1, 1));
        gameMapRepository.save(gameMap);
        
        // Check the updated state
        findedMap = gameMapRepository.findByIdAndGetChunks(gameMap.getId(),
                                                           chunks.keySet().stream().toList());
        assertThat(findedMap).isPresent();
        findChunks = findedMap.get().getChunks();
        chunk = findChunks.get(new Vector2C(0,0));
        assertThat(chunk.getEnemies().get(new Vector2(1, 1))).isNotNull();
    }
    
    @Test
    public void givenGameMap_whenSave_thenInventoryIsSaved() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> map = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getInventory()).isEqualTo(MapFactory.GAME_MAP.getInventory());
    }
    
    @Test
    public void givenGameMap_whenSave_thenCombatTerrainIsSaved() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> map = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(map).isPresent();
        assertThat(map.get().getCombatTerrain()).isEqualTo(MapFactory.GAME_MAP.getCombatTerrain());
    }
    
    @Test
    public void givenExistingMap_whenDeleteById_thenMapIsDeleted() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        gameMapRepository.deleteById(MapFactory.GAME_MAP.getId());
        
        Optional<GameMap> result = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(result).isEmpty();
    }
    
    @Test
    public void givenMapWithChunks_whenDeleteById_thenChunksAreDeleted() {
        gameMapRepository.save(MapFactory.GAME_MAP_WITH_CHUNKS);
        
        gameMapRepository.deleteById(MapFactory.GAME_MAP_WITH_CHUNKS.getId());
        
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP_WITH_CHUNKS.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        
        assertThat(chunks.size()).isEqualTo(0);
    }
}