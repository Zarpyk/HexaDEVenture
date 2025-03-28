package com.hexadeventure.model.inventory.materials;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.map.resources.ResourceType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Material extends Item {
    private ResourceType materialType;
    
    public Material(String name, ResourceType materialType) {
        super(name, ItemType.MATERIAL, 1);
        this.materialType = materialType;
        setId(Integer.toString(hashCode()));
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + hashCode();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), materialType);
    }
}
