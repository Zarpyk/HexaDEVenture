package com.hexadeventure.adapter.out.settings.json.material;

import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.map.resources.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialJson extends ItemJson<Material> {
    private MaterialJsonType materialType;
    
    public static ResourceType getID(MaterialJson materialJson) {
        return ResourceType.values()[materialJson.getMaterialType().ordinal()];
    }
    
    public Material toModel() {
        return new Material(getName(), getSkin(), ResourceType.values()[materialType.ordinal()]);
    }
}
