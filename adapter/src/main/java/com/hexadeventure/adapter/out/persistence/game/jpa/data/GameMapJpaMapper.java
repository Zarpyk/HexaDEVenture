package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.combat.CombatTerrainJpaMapper;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.inventory.InventoryJpaMapper;
import com.hexadeventure.model.map.GameMap;

public class GameMapJpaMapper {
    public static GameMapJpaEntity toEntity(GameMap model) {
        GameMapJpaEntity entity = new GameMapJpaEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserEmail());
        entity.setSeed(model.getSeed());
        entity.setMapSize(model.getSize());
        entity.setMainCharacter(MainCharacterJpaMapper.toEntity(model.getMainCharacter()));
        entity.setInventory(InventoryJpaMapper.toEntity(model.getInventory()));
        entity.setCombatTerrain(CombatTerrainJpaMapper.toEntity(model.getCombatTerrain()));
        return entity;
    }
    
    public static GameMap toModel(GameMapJpaEntity entity) {
        return new GameMap(entity.getId(),
                           entity.getUserId(),
                           entity.getSeed(),
                           entity.getMapSize(),
                           null,
                           MainCharacterJpaMapper.toModel(entity.getMainCharacter()),
                           InventoryJpaMapper.toModel(entity.getInventory()),
                           CombatTerrainJpaMapper.toModel(entity.getCombatTerrain()));
    }
}
