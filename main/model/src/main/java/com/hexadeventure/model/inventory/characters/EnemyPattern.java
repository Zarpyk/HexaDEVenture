package com.hexadeventure.model.inventory.characters;

public record EnemyPattern(float minThreshold,
                           EnemySetting[][] enemies,
                           Loot[] loot) implements Comparable<EnemyPattern> {
    @Override
    public int compareTo(EnemyPattern o) {
        return Float.compare(this.minThreshold, o.minThreshold);
    }
}
