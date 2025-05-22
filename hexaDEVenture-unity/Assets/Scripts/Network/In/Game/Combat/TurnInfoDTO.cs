using System.Collections.Generic;
using Network.In.Game.DTOEnum;

namespace Network.In.Game.Combat {
    public record TurnInfoDTO(
        CombatAction Action,
        bool IsEnemyTurn,
        int Row,
        int Column,
        IReadOnlyList<CharacterStatusChangeDTO> CharacterStatus,
        int TargetRow,
        int TargetColumn,
        IReadOnlyList<CharacterStatusChangeDTO> TargetStatus
    );
}