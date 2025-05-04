package com.hexadeventure.adapter.in.rest.game.map;

import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;

public record CellDataDTO(Vector2DTO position,
                          CellType type) {
    public static CellDataDTO fromModel(CellData model) {
        if(model == null) return null;
        return new CellDataDTO(Vector2DTO.fromModel(model.getPosition()), model.getType());
    }
}
