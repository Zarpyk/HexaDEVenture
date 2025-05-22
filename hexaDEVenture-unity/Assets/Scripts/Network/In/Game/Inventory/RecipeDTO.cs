using Network.In.Game.DTOEnum;

namespace Network.In.Game.Inventory {
    public record RecipeDTO(
        string ResultId,
        ItemType ResultType,
        int ResultCount,
        RecipeResourceDTO[] Materials,
        int CraftableCount
    );
}