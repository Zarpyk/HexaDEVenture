package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class GameMapMongoRepository implements GameMapRepository {
    
    private final GameMapMongoSDRepository repo;
    private final GridFsOperations gridFsOperations;
    
    public GameMapMongoRepository(GameMapMongoSDRepository repo, GridFsOperations gridFsOperations) {
        this.repo = repo;
        this.gridFsOperations = gridFsOperations;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapMongoEntity> map = repo.findById(id);
        return map.map(x -> GameMapMongoMapper.toModel(x, gridFsOperations));
    }
    
    @Override
    public void save(GameMap newMap) {
        GameMapMongoEntity mapEntity = GameMapMongoMapper.toEntity(newMap, repo, gridFsOperations);
        repo.save(mapEntity);
    }
    
    @Override
    public void deleteById(String mapId) {
        Optional<GameMapMongoEntity> map = repo.findById(mapId);
        if(map.isPresent() && map.get().getGridFileId() != null) {
            gridFsOperations.delete(query(whereFilename().is(map.get().getGridFileId() + ".json")));
        }
        repo.deleteById(mapId);
    }
}
