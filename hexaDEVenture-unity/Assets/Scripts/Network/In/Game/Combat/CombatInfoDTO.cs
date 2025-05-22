namespace Network.In.Game.Combat {
    public record CombatInfoDTO(
        CharacterDataDTO[][] PlayerCharacters,
        CharacterDataDTO[][] Enemies
    );
}