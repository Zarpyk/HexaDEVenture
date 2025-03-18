package com.hexadeventure.adapter.out.persistence.users.jpa;

import com.hexadeventure.model.user.User;

public class UserJpaMapper {
    public static UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setMapId(user.getMapId());
        return entity;
    }
    
    public static User toModel(UserJpaEntity entity) {
        return new User(entity.getId(),
                        entity.getEmail(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getMapId());
    }
}
