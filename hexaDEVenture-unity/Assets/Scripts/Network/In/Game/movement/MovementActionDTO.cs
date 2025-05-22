using System.Collections.Generic;
using Network.In.Game.Map;

namespace Network.In.Game.movement {
    public record MovementActionDTO(
        Vector2DTO OriginalPosition,
        Vector2DTO TargetPosition,
        ResourceActionDTO Resource,
        IReadOnlyList<EnemyMovementDTO> EnemyMovements,
        bool StartCombat
    );
}