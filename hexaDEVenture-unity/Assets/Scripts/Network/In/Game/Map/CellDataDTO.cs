using Network.In.Game.DTOEnum;

namespace Network.In.Game.Map {
    public record CellDataDTO(
        Vector2DTO Position,
        CellType Type
    );
}