package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.Vector2;

public class CellDataJpaMapper {
    public static CellDataJpaEntity toEntity(CellData model) {
        CellDataJpaEntity entity = new CellDataJpaEntity();
        entity.setX(model.getPosition().x);
        entity.setY(model.getPosition().y);
        entity.setType(model.getType().ordinal());
        return entity;
    }
    
    public static CellData toModel(CellDataJpaEntity entity) {
        return new CellData(new Vector2(entity.getX(), entity.getY()), CellType.values()[entity.getType()]);
    }
}
