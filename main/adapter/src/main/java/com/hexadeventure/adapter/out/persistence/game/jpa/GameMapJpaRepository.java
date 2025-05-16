package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.GameMapJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.GameMapJpaMapper;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2C;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public class GameMapJpaRepository implements GameMapRepository {
    
    private final GameMapJpaSDRepository repo;
    private final ChunkRepository chunkRepository;
    
    public GameMapJpaRepository(GameMapJpaSDRepository repo, ChunkRepository chunkRepository) {
        this.repo = repo;
        this.chunkRepository = chunkRepository;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapJpaEntity> map = repo.findById(id);
        return map.map(GameMapJpaMapper::toModel);
    }
    
    @Override
    @SuppressWarnings("DuplicatedCode")
    public Optional<GameMap> findByIdAndGetChunks(String id, Collection<Vector2C> positions) {
        Optional<GameMapJpaEntity> map = repo.findById(id);
        List<Chunk> chunks = map.isPresent() ? chunkRepository.findChunks(id, positions) : List.of();
        Optional<GameMap> gameMap = map.map(GameMapJpaMapper::toModel);
        gameMap.ifPresent(value -> chunks.forEach(chunk -> value.setChunk(chunk.getPosition(), chunk)));
        return gameMap;
    }
    
    @Override
    public Map<Vector2C, Chunk> findMapChunks(String id, Collection<Vector2C> positions) {
        List<Chunk> chunks = chunkRepository.findChunks(id, positions);
        Map<Vector2C, Chunk> chunkMap = new HashMap<>();
        chunks.forEach(chunk -> chunkMap.put(chunk.getPosition(), chunk));
        return chunkMap;
    }
    
    @Override
    @Transactional
    public void save(GameMap gameMap) {
        GameMapJpaEntity mapEntity = GameMapJpaMapper.toEntity(gameMap);
        chunkRepository.saveChunks(gameMap.getId(), gameMap.getChunks().values());
        repo.save(mapEntity);
    }
    
    @Override
    @Transactional
    public void deleteById(String mapId) {
        chunkRepository.deleteByMapId(mapId);
        repo.deleteById(mapId);
    }
}
