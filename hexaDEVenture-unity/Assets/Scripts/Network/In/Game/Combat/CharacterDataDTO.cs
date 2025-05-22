namespace Network.In.Game.Combat {
    public record CharacterDataDTO(
        string ID,
        string Name,
        double Health,
        double Speed,
        WeaponDataDTO Weapon,
        CharacterChangedStatDTO ChangedStats
    );
}