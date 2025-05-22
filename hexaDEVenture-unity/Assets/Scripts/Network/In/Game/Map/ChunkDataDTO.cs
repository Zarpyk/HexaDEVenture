using System.Collections.Generic;

namespace Network.In.Game.Map {
    public record ChunkDataDTO(IReadOnlyList<ChunkDTO> Chunks, Vector2DTO mainCharacterPosition);
}