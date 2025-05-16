package com.hexadeventure.adapter.out.persistence.map;

import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.Chunk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractChunkRepositoryTest {
    @Autowired
    public GameMapRepository gameMapRepository;
    @Autowired
    public ChunkRepository chunkRepository;
    
    @AfterEach
    public void afterEach() {
        gameMapRepository.deleteById(MapFactory.GAME_MAP.getId());
        chunkRepository.deleteByMapId(MapFactory.GAME_MAP.getId());
    }
    
    @Test
    public void givenMapIdAndPositions_whenSaveChunks_thenSaved() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        chunkRepository.saveChunks(MapFactory.GAME_MAP.getId(),
                                   List.of(MapFactory.CHUNK1,
                                           MapFactory.CHUNK2,
                                           MapFactory.CHUNK3,
                                           MapFactory.CHUNK4));
        
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        
        assertThat(chunks).isNotNull();
        assertThat(chunks.size()).isEqualTo(4);
    }
    
    @Test
    public void givenMapIdAndPositions_whenFindChunks_thenFound() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        chunkRepository.saveChunks(MapFactory.GAME_MAP.getId(),
                                   List.of(MapFactory.CHUNK1,
                                           MapFactory.CHUNK2,
                                           MapFactory.CHUNK3,
                                           MapFactory.CHUNK4));
        
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        
        assertThat(chunks).isNotNull();
        assertThat(chunks.size()).isEqualTo(4);
    }
    
    @Test
    public void givenNotSavedChunks_whenFindChunks_thenReturnEmptyList() {
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        
        assertThat(chunks.size()).isEqualTo(0);
    }
    
    @Test
    public void givenMapId_whenDeleteChunks_thenDeleted() {
        gameMapRepository.save(MapFactory.GAME_MAP);
        chunkRepository.saveChunks(MapFactory.GAME_MAP.getId(),
                                   List.of(MapFactory.CHUNK1,
                                           MapFactory.CHUNK2,
                                           MapFactory.CHUNK3,
                                           MapFactory.CHUNK4));
        
        chunkRepository.deleteByMapId(MapFactory.GAME_MAP.getId());
        
        List<Chunk> chunks = chunkRepository.findChunks(MapFactory.GAME_MAP.getId(),
                                                        List.of(MapFactory.CHUNK1.getPosition(),
                                                                MapFactory.CHUNK2.getPosition(),
                                                                MapFactory.CHUNK3.getPosition(),
                                                                MapFactory.CHUNK4.getPosition()));
        
        assertThat(chunks.size()).isEqualTo(0);
    }
}