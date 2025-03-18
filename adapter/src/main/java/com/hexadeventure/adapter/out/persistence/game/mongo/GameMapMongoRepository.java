package com.hexadeventure.adapter.out.persistence.game.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.GameMap;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class GameMapMongoRepository implements GameMapRepository {
    
    private final GameMapMongoSDRepository repo;
    
    public GameMapMongoRepository(GameMapMongoSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapMongoEntity> user = repo.findById(id);
        return user.map(GameMapMongoMapper::toModel);
    }
    
    @Override
    public void save(GameMap newMap) {
        repo.save(GameMapMongoMapper.toEntity(newMap));
    }
}
