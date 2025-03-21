package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.model.enemies.Enemy;

public class EnemyJpaMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .build();
        objectMapper.activateDefaultTyping(ptv,
                                           ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                           JsonTypeInfo.As.PROPERTY);
    }
    
    public static EnemyJpaEntity toEntity(Enemy model) {
        EnemyJpaEntity entity = new EnemyJpaEntity();
        entity.setX(model.getPosition().x);
        entity.setY(model.getPosition().y);
        try {
            String json = objectMapper.writeValueAsString(model);
            entity.setData(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
    
    public static Enemy toModel(EnemyJpaEntity entity) {
        try {
            if(entity == null || entity.getData() == null) {
                return null;
            }
            return objectMapper.readValue(entity.getData(), Enemy.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
