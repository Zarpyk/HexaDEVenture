package com.hexadeventure.adapter.in.rest.game.dto.out.map;

import com.hexadeventure.model.map.Chunk;

import java.util.Arrays;
import java.util.List;

public record ChunkDTO(Vector2DTO chunkPosition, CellDataDTO[][] cells, List<ResourceDTO> resources,
                       List<EnemyDTO> enemies) {
    public static ChunkDTO fromModel(Chunk value) {
        CellDataDTO[][] cellDataDTOs = Arrays.stream(value.getCells())
                                             .map(cellData -> Arrays.stream(cellData)
                                                                    .map(CellDataDTO::fromModel)
                                                                    .toArray(CellDataDTO[]::new))
                                             .toArray(CellDataDTO[][]::new);
        
        List<ResourceDTO> resourcesDTO = value.getResources().values().stream().map(ResourceDTO::fromModel).toList();
        
        List<EnemyDTO> enemiesDTO = value.getEnemies().values().stream().map(EnemyDTO::fromModel).toList();
        
        return new ChunkDTO(Vector2DTO.fromModel(value.getPosition()), cellDataDTOs, resourcesDTO, enemiesDTO);
    }
}
