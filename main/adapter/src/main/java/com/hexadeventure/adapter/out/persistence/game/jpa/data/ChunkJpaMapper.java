package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hexadeventure.adapter.utils.Vector2CDeserializer;
import com.hexadeventure.adapter.utils.Vector2Deserializer;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.characters.Loot;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import com.hexadeventure.model.map.resources.Resource;

import java.util.HashMap;
import java.util.Map;

public class ChunkJpaMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .allowIfSubType(PlayableCharacter[][].class)
                                                                    .allowIfSubType(PlayableCharacter[].class)
                                                                    .allowIfSubType(Loot[].class)
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
    
    public static ChunkJpaEntity toEntity(String mapId, Chunk model) {
        ChunkJpaEntity entity = new ChunkJpaEntity();
        entity.setId(model.getId());
        entity.setMapId(mapId);
        entity.setX(model.getPosition().x);
        entity.setY(model.getPosition().y);
        try {
            String json = objectMapper.writeValueAsString(model.getCells());
            entity.setCellsJson(json);
            json = objectMapper.writeValueAsString(model.getResources());
            entity.setResourcesJson(json);
            json = objectMapper.writeValueAsString(model.getEnemies());
            entity.setEnemiesJson(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
    
    public static Chunk toModel(ChunkJpaEntity entity) {
        try {
            CellData[][] grid = objectMapper.readValue(entity.getCellsJson(), CellData[][].class);
            Map<Vector2, Resource> resourceHashMap = objectMapper.readValue(entity.getResourcesJson(),
                                                                            new TypeReference<>() {});
            Map<Vector2, Enemy> enemyHashMap = objectMapper.readValue(entity.getEnemiesJson(),
                                                                      new TypeReference<>() {});
            
            return new Chunk(entity.getId(),
                             new Vector2C(entity.getX(), entity.getY()),
                             grid,
                             resourceHashMap,
                             enemyHashMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
