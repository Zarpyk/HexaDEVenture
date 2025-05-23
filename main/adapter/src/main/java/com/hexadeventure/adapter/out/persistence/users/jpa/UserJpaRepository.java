package com.hexadeventure.adapter.out.persistence.users.jpa;

import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<User> findById(String userId) {
        Optional<UserJpaEntity> jpaEntity = repo.findById(userId);
        return jpaEntity.map(UserJpaMapper::toModel);
    }
    
    @Override
    @Transactional
    public void save(User user) {
        repo.save(UserJpaMapper.toEntity(user));
    }
    
    @Override
    @Transactional
    public void deleteAll() {
        repo.deleteAll();
    }
    
    @Override
    @Transactional
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }
}
