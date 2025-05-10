package com.hexadeventure.adapter.out.persistence.users.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public interface UserMongoSDRepository extends MongoRepository<UserMongoEntity, String> {
    Optional<UserMongoEntity> findByEmail(String email);
    
    void deleteByEmail(String email);
}
