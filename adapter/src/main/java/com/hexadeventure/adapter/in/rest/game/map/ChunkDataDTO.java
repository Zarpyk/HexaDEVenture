package com.hexadeventure.adapter.in.rest.game.map;

import com.hexadeventure.model.map.ChunkData;

import java.util.Map;
import java.util.stream.Collectors;

public record ChunkDataDTO(Map<Vector2DTO, ChunkDTO> chunks) {
    public static ChunkDataDTO fromModel(ChunkData model) {
        return new ChunkDataDTO(model.chunks().entrySet().stream()
                                     .collect(Collectors.toMap(
                                             entry -> Vector2DTO.fromModel(entry.getKey()),
                                             entry -> ChunkDTO.fromModel(entry.getValue())
                                     )));
    }
}
