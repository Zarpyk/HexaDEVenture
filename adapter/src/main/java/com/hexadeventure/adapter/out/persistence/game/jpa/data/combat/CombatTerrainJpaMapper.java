package com.hexadeventure.adapter.out.persistence.game.jpa.data.combat;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;

public class CombatTerrainJpaMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .allowIfSubType(PlayableCharacter[][].class)
                                                                    .allowIfSubType(PlayableCharacter[].class)
                                                                    .build();
        
        objectMapper.activateDefaultTyping(ptv,
                                           ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                           JsonTypeInfo.As.PROPERTY);
    }
    
    public static CombatTerrainJpaEntity toEntity(CombatTerrain model) {
        try {
            CombatTerrainJpaEntity entity = new CombatTerrainJpaEntity();
            entity.setId(model.getId());
            entity.setRowSize(model.getRowSize());
            entity.setColumnSize(model.getColumnSize());
            
            String playerJson = objectMapper.writeValueAsString(model.getPlayerTerrain());
            entity.setPlayerTerrain(playerJson);
            
            String enemiesJson = objectMapper.writeValueAsString(model.getEnemyTerrain());
            entity.setEnemyTerrain(enemiesJson);
            
            return entity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static CombatTerrain toModel(CombatTerrainJpaEntity entity) {
        try {
            PlayableCharacter[][] player = objectMapper.readValue(entity.getPlayerTerrain(),
                                                                  PlayableCharacter[][].class);
            PlayableCharacter[][] enemies = objectMapper.readValue(entity.getEnemyTerrain(),
                                                                   PlayableCharacter[][].class);
            return new CombatTerrain(entity.getId(), entity.getRowSize(), entity.getColumnSize(), player, enemies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
