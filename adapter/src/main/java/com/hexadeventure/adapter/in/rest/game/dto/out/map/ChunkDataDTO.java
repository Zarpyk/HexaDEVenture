package com.hexadeventure.adapter.in.rest.game.dto.out.map;

import com.hexadeventure.model.map.ChunkData;

import java.util.List;

public record ChunkDataDTO(List<ChunkDTO> chunks, Vector2DTO mainCharacterPosition) {
    public static ChunkDataDTO fromModel(ChunkData model) {
        return new ChunkDataDTO(model.chunks()
                                     .values()
                                     .stream()
                                     .map((ChunkDTO::fromModel))
                                     .toList(),
                                Vector2DTO.fromModel(model.mainCharacter().getPosition()));
    }
}
