using Network.In.Game.DTOEnum;

namespace Network.In.Game.Combat {
    public record WeaponDataDTO(
        string ID,
        string Name,
        ItemType ItemType,
        int Skin,
        int Count,
        WeaponType WeaponType,
        double Damage,
        double MeleeDefense,
        double RangedDefense,
        int Cooldown,
        double AggroGeneration,
        int InitialAggro,
        double HealingPower,
        double HypnotizationPower
    );
}