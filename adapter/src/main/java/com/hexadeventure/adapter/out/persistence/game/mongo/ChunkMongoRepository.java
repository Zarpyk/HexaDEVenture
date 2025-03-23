package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.adapter.out.persistence.game.mongo.data.ChunkMongoEntity;
import com.hexadeventure.adapter.out.persistence.game.mongo.data.ChunkMongoMapper;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class ChunkMongoRepository implements ChunkRepository {
    
    private final ChunkMongoSDRepository repo;
    private final GridFsOperations gridFsOperations;
    
    public ChunkMongoRepository(ChunkMongoSDRepository repo, GridFsOperations gridFsOperations) {
        this.repo = repo;
        this.gridFsOperations = gridFsOperations;
    }
    
    @Override
    public List<Chunk> findChunks(String mapId, Collection<Vector2> positions) {
        if(!repo.existsByMapId(mapId)) return List.of();
        List<Chunk> chunks = new ArrayList<>();
        for (Vector2 position : positions) {
            repo.findByMapIdAndXAndY(mapId, position.x, position.y)
                .ifPresent(chunk -> chunks.add(ChunkMongoMapper.toModel(chunk, gridFsOperations)));
        }
        return chunks;
    }
    
    @Override
    public void saveChunks(String mapId, Collection<Chunk> chunks) {
        List<ChunkMongoEntity> chunkEntities = chunks.stream()
                                                     .map(chunk -> ChunkMongoMapper.toEntity(mapId,
                                                                                             chunk,
                                                                                             repo,
                                                                                             gridFsOperations))
                                                     .toList();
        repo.saveAll(chunkEntities);
    }
    
    @Override
    public void deleteByMapId(String mapId) {
        repo.deleteByMapId(mapId);
    }
}
