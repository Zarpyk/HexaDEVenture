package com.hexadeventure.adapter.in.rest.game.map;

import com.hexadeventure.model.map.Chunk;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record ChunkDTO(Vector2DTO chunkPosition, CellDataDTO[][] cells,
                       Map<Vector2DTO, ResourceDTO> resources,
                       Map<Vector2DTO, EnemyDTO> enemies) {
    public static ChunkDTO fromModel(Chunk value) {
        CellDataDTO[][] cellDataDTOs = Arrays.stream(value.getCells())
                                             .map(cellData -> Arrays.stream(cellData)
                                                                    .map(CellDataDTO::fromModel)
                                                                    .toArray(CellDataDTO[]::new))
                                             .toArray(CellDataDTO[][]::new);
        
        Map<Vector2DTO, ResourceDTO> resourcesDTO = value.getResources().entrySet().stream()
                                                         .collect(Collectors.toMap(
                                                                 entry -> Vector2DTO.fromModel(entry.getKey()),
                                                                 entry -> ResourceDTO.fromModel(entry.getValue())
                                                         ));
        
        Map<Vector2DTO, EnemyDTO> enemiesDTO = value.getEnemies().entrySet().stream()
                                                    .collect(Collectors.toMap(
                                                            entry -> Vector2DTO.fromModel(entry.getKey()),
                                                            entry -> EnemyDTO.fromModel(entry.getValue())
                                                    ));
        
        return new ChunkDTO(Vector2DTO.fromModel(value.getPosition()),
                            cellDataDTOs,
                            resourcesDTO,
                            enemiesDTO);
    }
}
