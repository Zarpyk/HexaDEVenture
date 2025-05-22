namespace Network.In.Game.Combat {
    public record CharacterChangedStatDTO(
        double Health,
        bool Hypnotized,
        double BoostHealth,
        double BoostSpeed,
        double BoostStrength,
        double BoostDefense
    );
}