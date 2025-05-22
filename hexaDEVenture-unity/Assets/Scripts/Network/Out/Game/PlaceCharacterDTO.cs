namespace Network.Out.Game {
    public record PlaceCharacterDTO(
        int Row,
        int Column,
        string CharacterId
    );
}