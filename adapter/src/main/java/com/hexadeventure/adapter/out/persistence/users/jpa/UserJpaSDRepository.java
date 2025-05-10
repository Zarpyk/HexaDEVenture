package com.hexadeventure.adapter.out.persistence.users.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public interface UserJpaSDRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByEmail(String email);
    
    void deleteByEmail(String email);
}
