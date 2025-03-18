package com.hexadeventure.adapter.out.persistence.users.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "jpa")
@Repository
public class UserJpaRepository implements UserRepository {
    
    private final UserJpaSDRepository repo;
    
    public UserJpaRepository(UserJpaSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserJpaEntity> jpaEntity = repo.findByEmail(email);
        return jpaEntity.map(UserJpaMapper::toModel);
    }
    
    @Override
    public void save(User user) {
        repo.save(UserJpaMapper.toEntity(user));
    }
    
    @Override
    public void deleteAll() {
        repo.deleteAll();
    }
    
    @Override
    public void updateMapIdByEmail(String email, String mapId) {
        repo.updateMapIdByEmail(email, mapId);
    }
}
