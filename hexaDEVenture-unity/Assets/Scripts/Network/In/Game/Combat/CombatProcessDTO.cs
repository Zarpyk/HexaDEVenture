using System.Collections.Generic;

namespace Network.In.Game.Combat {
    public record CombatProcessDTO(
        IReadOnlyList<TurnInfoDTO> Turns,
        bool CombatFinished,
        bool IsBossBattle,
        bool Lose
    );
}