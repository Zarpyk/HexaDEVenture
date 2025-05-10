package com.hexadeventure.model.map.resources;

import java.util.Arrays;

public enum ResourceType {
    WOOD(0.0),
    STONE(0.2),
    IRON(0.3),
    STEEL(0.4),
    GOLD(0.5);
    
    // Add minThreshold to each resource type
    final double minThreshold;
    
    ResourceType(double minThreshold) {
        this.minThreshold = minThreshold;
    }
    
    public static ResourceType[] getResourcesBelowThreshold(double threshold) {
        ResourceType[] resources = ResourceType.values();
        return Arrays.stream(resources)
                     .filter(resourceType -> resourceType.minThreshold < threshold)
                     .toArray(ResourceType[]::new);
    }
}
