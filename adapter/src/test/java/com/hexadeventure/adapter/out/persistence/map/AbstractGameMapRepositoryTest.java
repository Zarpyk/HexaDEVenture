package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

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
        assertThat(map.get()).isEqualTo(MapFactory.GAME_MAP);
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
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Vector2 newPos = new Vector2(10, 10);
        GameMap map = gameMapRepository.findById(MapFactory.GAME_MAP.getId()).orElse(null);
        assertThat(map).isNotNull();
        assertThat(map.getMainCharacter().getPosition()).isNotEqualTo(newPos);
        
        MapFactory.GAME_MAP.initMainCharacter(newPos);
        gameMapRepository.save(MapFactory.GAME_MAP);
        
        Optional<GameMap> updatedMap = gameMapRepository.findById(MapFactory.GAME_MAP.getId());
        assertThat(updatedMap).isPresent();
        GameMap mapObject = updatedMap.get();
        assertThat(mapObject.getMainCharacter().getPosition()).isEqualTo(newPos);
        
        MapFactory.GAME_MAP.initMainCharacter(new Vector2(MapFactory.TEST_SIZE / 2, MapFactory.TEST_SIZE / 2));
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