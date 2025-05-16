package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaMapper;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2C;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public class ChunkJpaRepository implements ChunkRepository {
    
    private final ChunkJpaSDRepository repo;
    
    public ChunkJpaRepository(ChunkJpaSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public List<Chunk> findChunks(String mapId, Collection<Vector2C> positions) {
        if(!repo.existsByMapId(mapId)) return List.of();
        List<Chunk> chunks = new ArrayList<>();
        for (Vector2C position : positions) {
            repo.findByMapIdAndXAndY(mapId, position.x, position.y)
                .ifPresent(chunk -> chunks.add(ChunkJpaMapper.toModel(chunk)));
        }
        return chunks;
    }
    
    @Override
    @Transactional
    public void saveChunks(String mapId, Collection<Chunk> chunks) {
        // Parallelize this
        List<ChunkJpaEntity> chunkEntities = chunks.parallelStream()
                                                   .map(chunk -> ChunkJpaMapper.toEntity(mapId, chunk))
                                                   .collect(Collectors.toCollection(
                                                           () -> new ArrayList<>(chunks.size())));
        repo.saveAll(chunkEntities);
    }
    
    @Override
    @Transactional
    public void deleteByMapId(String mapId) {
        repo.deleteByMapId(mapId);
    }
}
