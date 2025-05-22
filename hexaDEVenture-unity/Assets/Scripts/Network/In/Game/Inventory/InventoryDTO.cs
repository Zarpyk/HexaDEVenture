using System.Collections.Generic;

namespace Network.In.Game.Inventory {
    public record InventoryDTO(
        IReadOnlyList<ItemDTO> Items,
        IReadOnlyList<CharacterDTO> Characters
    );
}