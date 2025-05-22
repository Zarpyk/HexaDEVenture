using System.Collections.Generic;

namespace Network.In.Game.movement {
    public record MovementResponseDTO(IReadOnlyList<MovementActionDTO> Actions, bool NewChunks);
}