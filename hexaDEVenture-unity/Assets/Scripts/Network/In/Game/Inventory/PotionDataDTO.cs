using Network.In.Game.DTOEnum;

namespace Network.In.Game.Inventory {
    public record PotionDataDTO(
        string ID,
        string Name,
        ItemType ItemType,
        int Skin,
        int Count,
        PotionType PotionType,
        double PotionPower
    );
}