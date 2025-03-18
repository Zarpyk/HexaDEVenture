package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexadeventure.model.map.CellData;

public class CellDataJpaMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
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
            return objectMapper.readValue(entity.getData(), CellData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
