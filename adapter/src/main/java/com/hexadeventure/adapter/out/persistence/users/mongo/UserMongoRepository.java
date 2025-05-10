package com.hexadeventure.adapter.out.persistence.users.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class UserMongoRepository implements UserRepository {
    private final UserMongoSDRepository repo;
    
    public UserMongoRepository(UserMongoSDRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserMongoEntity> mongoEntity = repo.findByEmail(email);
        return mongoEntity.map(UserMongoMapper::toModel);
    }
    
    @Override
    public void save(User user) {
        repo.save(UserMongoMapper.toEntity(user));
    }
    
    @Override
    public void deleteAll() {
        repo.deleteAll();
    }
    
    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }
}
