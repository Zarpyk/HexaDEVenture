package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hexadeventure.adapter.utils.Vector2Deserializer;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class GameMapMongoMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .allowIfSubType(CellData[][].class)
                                                                    .allowIfSubType(CellData[].class)
                                                                    .allowIfSubType(HashMap.class)
                                                                    .build();
        objectMapper.activateDefaultTyping(ptv,
                                           ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                           JsonTypeInfo.As.PROPERTY);
        
        // From: https://stackoverflow.com/a/44210009/11451105
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(Vector2.class, new Vector2Deserializer());
        objectMapper.registerModule(simpleModule);
    }
    
    public static GameMapMongoEntity toEntity(GameMap model, GameMapMongoSDRepository repo,
                                              GridFsOperations gridFsOperations) {
        GameMapMongoEntity mongoEntity = new GameMapMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setSeed(model.getSeed());
        mongoEntity.setGridSize(model.getMapSize());
        mongoEntity.setMainCharacter(MainCharacterMongoMapper.toEntity(model.getMainCharacter()));
        
        try {
            Optional<GameMapMongoEntity> oldMap = repo.findById(model.getId());
            
            // Store grid
            String gridFileId = storeData(model.getGrid(),
                                          oldMap.map(GameMapMongoEntity::getGridFileId).orElse(null),
                                          gridFsOperations);
            mongoEntity.setGridFileId(gridFileId);
            
            // Store resources
            String resourcesFileId = storeData(model.getResources(),
                                               oldMap.map(GameMapMongoEntity::getResourcesFileId).orElse(null),
                                               gridFsOperations);
            mongoEntity.setResourcesFileId(resourcesFileId);
            
            // Store enemies
            String enemiesFileId = storeData(model.getEnemies(),
                                             oldMap.map(GameMapMongoEntity::getEnemiesFileId).orElse(null),
                                             gridFsOperations);
            mongoEntity.setEnemiesFileId(enemiesFileId);
            
            return mongoEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static GameMap toModel(GameMapMongoEntity entity, GridFsOperations gridFsOperations) {
        try {
            GridFsResource gridResource = gridFsOperations.getResource(entity.getGridFileId() + ".json");
            CellData[][] grid = objectMapper.readValue(gridResource.getInputStream(), CellData[][].class);
            
            // From: https://stackoverflow.com/a/3076569/11451105
            GridFsResource resourceResource = gridFsOperations.getResource(entity.getResourcesFileId() + ".json");
            HashMap<Vector2, Resource> resources = objectMapper.readValue(resourceResource.getInputStream(),
                                                                          new TypeReference<>() {});
            
            GridFsResource enemiesResource = gridFsOperations.getResource(entity.getEnemiesFileId() + ".json");
            HashMap<Vector2, Enemy> enemies = objectMapper.readValue(enemiesResource.getInputStream(),
                                                                     new TypeReference<>() {});
            
            GameMap gameMap = new GameMap(entity.getId(),
                                          entity.getUserId(),
                                          entity.getSeed(),
                                          grid,
                                          resources,
                                          enemies);
            
            MainCharacterMongoEntity mainCharacter = entity.getMainCharacter();
            if(mainCharacter != null) {
                gameMap.initMainCharacter(new Vector2(mainCharacter.getX(), mainCharacter.getY()));
            }
            
            return gameMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String storeData(Object data, String existingFileId,
                                    GridFsOperations gridFsOperations) throws JsonProcessingException {
        byte[] jsonData = objectMapper.writeValueAsBytes(data);
        String fileId = (existingFileId != null) ? existingFileId : UUID.randomUUID().toString();
        
        gridFsOperations.store(
                new ByteArrayInputStream(jsonData),
                fileId + ".json",
                "application/json"
        );
        
        return fileId;
    }
}
