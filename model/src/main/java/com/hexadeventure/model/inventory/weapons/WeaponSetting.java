package com.hexadeventure.model.inventory.weapons;

public record WeaponSetting(String name,
                            int skin,
                            double minThreshold,
                            double maxThreshold,
                            WeaponType weaponType,
                            double minDamage,
                            double maxDamage,
                            double minMeleeDefense,
                            double maxMeleeDefense,
                            double minRangedDefense,
                            double maxRangedDefense,
                            int minCooldown,
                            int maxCooldown,
                            AggroGenType aggroGenType,
                            double extraAggroGeneration,
                            double minAggroGeneration,
                            double maxAggroGeneration,
                            int initialAggro,
                            double minHealingPower,
                            double maxHealingPower,
                            double minHypnotizationPower,
                            double maxHypnotizationPower) {
    
    public int compareTo(WeaponSetting weaponSetting) {
        return Double.compare(minThreshold, weaponSetting.minThreshold());
    }
}
