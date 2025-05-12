package com.hexadeventure.model.map.resources;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.utils.DoubleMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.SplittableRandom;

@Getter
@NoArgsConstructor
public class Resource {
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 6;
    
    private Vector2 position;
    private ResourceType type;
    private int count;
    
    public Resource(Vector2 position, double threshold, SplittableRandom random) {
        this.position = position;
        ResourceType[] resources = ResourceType.getResourcesBelowThreshold(threshold);
        this.type = resources[random.nextInt(resources.length)];
        int maxCount = (int) DoubleMapper.map(threshold, 0, 1, MIN_COUNT, MAX_COUNT);
        this.count = random.nextInt(MIN_COUNT, maxCount + 1);
    }
    
    public Resource(Vector2 position, ResourceType type, int count) {
        this.position = position;
        this.type = type;
        this.count = count;
    }
}
