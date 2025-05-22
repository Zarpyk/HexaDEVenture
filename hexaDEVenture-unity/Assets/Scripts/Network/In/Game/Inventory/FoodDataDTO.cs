using Network.In.Game.DTOEnum;

namespace Network.In.Game.Inventory {
    public record FoodDataDTO(
        string ID,
        string Name,
        ItemType ItemType,
        int Skin,
        int Count,
        double HealthPoints
    );
}