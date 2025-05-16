package com.hexadeventure.model.inventory.initial;

import com.hexadeventure.model.map.resources.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitialResourceTypeIdResourceData {
    private ResourceType id;
    private int count;
}
