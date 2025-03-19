package com.hexadeventure.adapter.out.persistence.users.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public interface UserJpaSDRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByEmail(String email);
    
    @Modifying
    @Query("update UserJpaEntity u set u.mapId = :email where u.email = :mapId")
    void updateMapIdByEmail(@Param("email") String email, @Param("mapId") String mapId);
    
    void deleteByEmail(String email);
}
