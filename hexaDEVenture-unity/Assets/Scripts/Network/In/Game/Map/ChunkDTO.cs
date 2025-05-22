using System.Collections.Generic;

namespace Network.In.Game.Map {
    public record ChunkDTO(
        Vector2DTO ChunkPosition,
        CellDataDTO[][] Cells,
        IReadOnlyList<ResourceDTO> Resources,
        IReadOnlyList<EnemyDTO> Enemies
    );
}