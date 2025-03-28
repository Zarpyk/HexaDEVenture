package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.adapter.out.persistence.common.GridFs;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.map.GameMap;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import java.io.IOException;
import java.util.HashMap;

public class GameMapMongoMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .allowIfSubType(HashMap.class)
                                                                    .build();
        objectMapper.activateDefaultTyping(ptv,
                                           ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                           JsonTypeInfo.As.PROPERTY);
    }
    
    public static GameMapMongoEntity toEntity(GameMap model, GridFsOperations gridFsOperations) {
        GameMapMongoEntity mongoEntity = new GameMapMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setUserId(model.getUserEmail());
        mongoEntity.setSeed(model.getSeed());
        mongoEntity.setMapSize(model.getSize());
        mongoEntity.setMainCharacter(MainCharacterMongoMapper.toEntity(model.getMainCharacter()));
        
        try {
            GridFs gridFs = new GridFs(objectMapper, gridFsOperations);
            
            // Save the inventory
            String inventoryFileId = gridFs.storeData(model.getInventory(),
                                                      mongoEntity.getInventoryFileId());
            mongoEntity.setInventoryFileId(inventoryFileId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        
        return mongoEntity;
    }
    
    public static GameMap toModel(GameMapMongoEntity entity, GridFsOperations gridFsOperations) {
        try {
            GridFs gridFs = new GridFs(objectMapper, gridFsOperations);
            Inventory inventory = gridFs.readData(entity.getInventoryFileId(), Inventory.class);
            
            return new GameMap(entity.getId(),
                               entity.getUserId(),
                               entity.getSeed(),
                               entity.getMapSize(),
                               null,
                               MainCharacterMongoMapper.toModel(entity.getMainCharacter()),
                               inventory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
