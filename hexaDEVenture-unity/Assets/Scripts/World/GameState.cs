using System;

namespace World {
    [Flags]
    public enum GameState {
        Pause = 1 << 0,
        Loading = 1 << 1,
        InGame = 1 << 2,
        InCombat = 1 << 3,
        InInventory = 1 << 4,
        CharacterPlaced = 1 << 5,
    }
}