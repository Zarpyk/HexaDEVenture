package com.hexadeventure.model.inventory.materials;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.map.resources.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Material extends Item {
    private ResourceType materialType;
    
    public Material(String name, int skin, ResourceType materialType) {
        super(name, ItemType.MATERIAL, skin);
        this.materialType = materialType;
        setId(materialType.toString());
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Material material)) return false;
        if(!super.equals(o)) return false;
        return materialType == material.materialType;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), materialType);
    }
}
