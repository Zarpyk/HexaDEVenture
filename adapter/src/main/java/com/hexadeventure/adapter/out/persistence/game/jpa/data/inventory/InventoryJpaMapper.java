package com.hexadeventure.adapter.out.persistence.game.jpa.data.inventory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;

import java.util.HashMap;

public class InventoryJpaMapper {
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
    
    public static InventoryJpaEntity toEntity(Inventory model) {
        try {
            InventoryJpaEntity entity = new InventoryJpaEntity();
            
            String itemsJson = objectMapper.writeValueAsString(model.getItems());
            entity.setId(model.getId());
            entity.setItemsJson(itemsJson);
            
            String charactersJson = objectMapper.writeValueAsString(model.getCharacters());
            entity.setId(model.getId());
            entity.setCharactersJson(charactersJson);
            
            return entity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Inventory toModel(InventoryJpaEntity entity) {
        try {
            HashMap<String, Item> items = objectMapper.readValue(entity.getItemsJson(),
                                                                 new TypeReference<>() {});
            HashMap<String, PlayableCharacter> characters = objectMapper.readValue(entity.getCharactersJson(),
                                                                                   new TypeReference<>() {});
            return new Inventory(entity.getId(), items, characters);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
