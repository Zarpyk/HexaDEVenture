package com.hexadeventure.adapter.out.persistence.game.mongo.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hexadeventure.adapter.out.persistence.game.mongo.ChunkMongoSDRepository;
import com.hexadeventure.adapter.utils.Vector2CDeserializer;
import com.hexadeventure.adapter.utils.Vector2Deserializer;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import com.hexadeventure.model.map.resources.Resource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ChunkMongoMapper {
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
        simpleModule.addKeyDeserializer(Vector2C.class, new Vector2CDeserializer());
        objectMapper.registerModule(simpleModule);
    }
    
    public static ChunkMongoEntity toEntity(String mapId, Chunk model, ChunkMongoSDRepository repo,
                                            GridFsOperations gridFsOperations) {
        ChunkMongoEntity mongoEntity = new ChunkMongoEntity();
        mongoEntity.setId(model.getId());
        mongoEntity.setMapId(mapId);
        mongoEntity.setX(model.getPosition().x);
        mongoEntity.setY(model.getPosition().y);
        
        try {
            Optional<ChunkMongoEntity> oldMap = repo.findById(model.getId());
            
            // Store grid
            String gridFileId = storeData(model.getCells(),
                                          oldMap.map(ChunkMongoEntity::getCellsFileId).orElse(null),
                                          gridFsOperations);
            mongoEntity.setCellsFileId(gridFileId);
            
            // Store resources
            String resourcesFileId = storeData(model.getResources(),
                                               oldMap.map(ChunkMongoEntity::getResourcesFileId).orElse(null),
                                               gridFsOperations);
            mongoEntity.setResourcesFileId(resourcesFileId);
            
            // Store enemies
            String enemiesFileId = storeData(model.getEnemies(),
                                             oldMap.map(ChunkMongoEntity::getEnemiesFileId).orElse(null),
                                             gridFsOperations);
            mongoEntity.setEnemiesFileId(enemiesFileId);
            
            return mongoEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Chunk toModel(ChunkMongoEntity entity, GridFsOperations gridFsOperations) {
        try {
            GridFsResource gridResource = gridFsOperations.getResource(entity.getCellsFileId() + ".json");
            CellData[][] grid = objectMapper.readValue(gridResource.getInputStream(), CellData[][].class);
            
            // From: https://stackoverflow.com/a/3076569/11451105
            GridFsResource resourceResource = gridFsOperations.getResource(entity.getResourcesFileId() + ".json");
            HashMap<Vector2, Resource> resources = objectMapper.readValue(resourceResource.getInputStream(),
                                                                          new TypeReference<>() {});
            
            GridFsResource enemiesResource = gridFsOperations.getResource(entity.getEnemiesFileId() + ".json");
            HashMap<Vector2, Enemy> enemies = objectMapper.readValue(enemiesResource.getInputStream(),
                                                                     new TypeReference<>() {});
            
            return new Chunk(entity.getId(),
                             new Vector2C(entity.getX(), entity.getY()),
                             grid,
                             resources,
                             enemies);
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
