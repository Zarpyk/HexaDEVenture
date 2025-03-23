package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public interface ChunkJpaSDRepository extends JpaRepository<ChunkJpaEntity, String> {
    boolean existsByMapId(String mapId);
    Optional<ChunkJpaEntity> findByMapIdAndXAndY(String mapId, int x, int y);
    void deleteByMapId(String mapId);
}