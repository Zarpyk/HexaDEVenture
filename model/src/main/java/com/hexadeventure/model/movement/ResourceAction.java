package com.hexadeventure.model.movement;

import com.hexadeventure.model.map.resources.ResourceType;

public record ResourceAction(ResourceType type, int amount) {
}
