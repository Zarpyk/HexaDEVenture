package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.GameMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public class GameMapJpaRepository implements GameMapRepository {
    
    private final GameMapJpaSDRepository repo;
    
    public GameMapJpaRepository(GameMapJpaSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapJpaEntity> map = repo.findById(id);
        return map.map(GameMapJpaMapper::toModel);
    }
    
    @Override
    @Transactional
    public void save(GameMap newMap) {
        repo.save(GameMapJpaMapper.toEntity(newMap));
    }
    
    @Override
    @Transactional
    public void deleteById(String mapId) {
        repo.deleteById(mapId);
    }
}
