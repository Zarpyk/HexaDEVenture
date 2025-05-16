package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.adapter.out.persistence.game.mongo.data.GameMapMongoEntity;
import com.hexadeventure.adapter.out.persistence.game.mongo.data.GameMapMongoMapper;
import com.hexadeventure.application.port.out.persistence.ChunkRepository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2C;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class GameMapMongoRepository implements GameMapRepository {
    
    private final GameMapMongoSDRepository repo;
    private final GridFsOperations gridFsOperations;
    
    private final ChunkRepository chunkRepository;
    
    public GameMapMongoRepository(GameMapMongoSDRepository repo, GridFsOperations gridFsOperations,
                                  ChunkRepository chunkRepository) {
        this.repo = repo;
        this.gridFsOperations = gridFsOperations;
        this.chunkRepository = chunkRepository;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapMongoEntity> map = repo.findById(id);
        return map.map(x -> GameMapMongoMapper.toModel(x, gridFsOperations));
    }
    
    @Override
    @SuppressWarnings("DuplicatedCode")
    public Optional<GameMap> findByIdAndGetChunks(String id, Collection<Vector2C> positions) {
        Optional<GameMapMongoEntity> map = repo.findById(id);
        List<Chunk> chunks = map.isPresent() ? chunkRepository.findChunks(id, positions) : List.of();
        Optional<GameMap> gameMap = map.map(x -> GameMapMongoMapper.toModel(x, gridFsOperations));
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
    public void save(GameMap gameMap) {
        GameMapMongoEntity mapEntity = GameMapMongoMapper.toEntity(gameMap, gridFsOperations);
        chunkRepository.saveChunks(gameMap.getId(), gameMap.getChunks().values());
        repo.save(mapEntity);
    }
    
    @Override
    public void deleteById(String mapId) {
        chunkRepository.deleteByMapId(mapId);
        repo.deleteById(mapId);
    }
}
