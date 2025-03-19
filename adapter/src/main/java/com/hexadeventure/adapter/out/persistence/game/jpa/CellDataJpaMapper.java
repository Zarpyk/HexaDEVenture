package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.model.map.CellData;

public class CellDataJpaMapper {
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
    
    public static CellDataJpaEntity toEntity(CellData model) {
        CellDataJpaEntity entity = new CellDataJpaEntity();
        try {
            String json = objectMapper.writeValueAsString(model);
            entity.setData(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
    
    public static CellData toModel(CellDataJpaEntity entity) {
        try {
            if(entity == null || entity.getData() == null) {
                return null;
            }
            return objectMapper.readValue(entity.getData(), CellData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
