package com.hexadeventure.model.map.resources;

import lombok.Getter;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.Vector2;

@Getter
public class ResourceCell extends CellData {
    private final ResourceType resourceType;
    private final int quantity;
    
    public ResourceCell(Vector2 position, double threshold) {
        super(position);
        type = CellType.RESOURCE;
        // TODO: Implement this
        this.resourceType = ResourceType.WOOD;
        this.quantity = 1;
    }
    
    public ResourceCell(Vector2 position, ResourceType resourceType, int quantity) {
        super(position);
        type = CellType.RESOURCE;
        this.resourceType = resourceType;
        this.quantity = quantity;
    }
}
