using Network.In.Game.DTOEnum;

public record MaterialDataDTO(string id,
                              string name,
                              ItemType itemType,
                              int skin,
                              int count,
                              ResourceType materialType) {
}
