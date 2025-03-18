package com.hexadeventure.adapter.out.persistence.game.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public interface GameMapJpaSDRepository extends JpaRepository<GameMapJpaEntity, String> {
}