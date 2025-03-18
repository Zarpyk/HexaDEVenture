package com.hexadeventure.adapter.out.persistence.users.mongo;

import com.hexadeventure.model.user.User;

public class UserMongoMapper {
    public static UserMongoEntity toEntity(User user) {
        UserMongoEntity entity = new UserMongoEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setMapId(user.getMapId());
        return entity;
    }
    
    public static User toModel(UserMongoEntity entity) {
        return new User(entity.getId(),
                        entity.getEmail(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getMapId());
    }
}
