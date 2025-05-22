using Network.In.Game.DTOEnum;

namespace Network.In.Game.Combat {
    public record CharacterStatusChangeDTO(
        CharacterStat StatChanged,
        double OldValue,
        double NewValue
    );
}