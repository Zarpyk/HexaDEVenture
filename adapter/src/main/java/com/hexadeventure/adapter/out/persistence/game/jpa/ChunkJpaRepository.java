package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaMapper;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public class ChunkJpaRepository implements ChunkRepository {
    
    private final ChunkJpaSDRepository repo;
    
    public ChunkJpaRepository(ChunkJpaSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public List<Chunk> findChunks(String mapId, Collection<Vector2> positions) {
        if(!repo.existsByMapId(mapId)) return List.of();
        List<Chunk> chunks = new ArrayList<>();
        for (Vector2 position : positions) {
            repo.findByMapIdAndXAndY(mapId, position.x, position.y)
                .ifPresent(chunk -> chunks.add(ChunkJpaMapper.toModel(chunk)));
        }
        return chunks;
    }
    
    @Override
    @Transactional
    public void saveChunks(String mapId, Collection<Chunk> chunks) {
        List<ChunkJpaEntity> chunkEntities = chunks.stream()
                                                   .map(chunk -> ChunkJpaMapper.toEntity(mapId, chunk))
                                                   .toList();
        repo.saveAll(chunkEntities);
    }
    
    @Override
    @Transactional
    public void deleteByMapId(String mapId) {
        repo.deleteByMapId(mapId);
    }
}
