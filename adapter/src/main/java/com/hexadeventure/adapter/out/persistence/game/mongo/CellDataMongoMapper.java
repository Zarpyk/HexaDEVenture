package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexadeventure.model.map.CellData;

public class CellDataMongoMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static CellDataMongoEntity toEntity(CellData model) {
        CellDataMongoEntity mongoEntity = new CellDataMongoEntity();
        try {
            String json = objectMapper.writeValueAsString(model);
            mongoEntity.setData(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return mongoEntity;
    }
    
    public static CellData toModel(CellDataMongoEntity entity) {
        try {
            return objectMapper.readValue(entity.getData(), CellData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
