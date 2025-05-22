using Network.In.Game.DTOEnum;

namespace Network.In.Game.Map {
    public record ResourceDTO(
        Vector2DTO Position,
        ResourceType Type,
        int Count
    );
}