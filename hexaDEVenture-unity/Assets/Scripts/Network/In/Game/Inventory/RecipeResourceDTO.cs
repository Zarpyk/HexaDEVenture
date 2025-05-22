using Network.In.Game.DTOEnum;

namespace Network.In.Game.Inventory {
    public record RecipeResourceDTO(
        string ID,
        ItemType Type,
        int Count
    );
}