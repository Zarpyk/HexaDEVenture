using Network.In.Game.DTOEnum;

namespace Network.In.Game.Inventory {
    public record ItemDTO(
        string ID,
        string Name,
        ItemType ItemType,
        int Skin,
        int Count
    );
}