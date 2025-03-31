package com.hexadeventure.adapter.out.settings.json.initial;

import com.hexadeventure.adapter.out.settings.json.material.MaterialJsonType;
import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.map.resources.ResourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitialMaterialTypeIDResourceJson {
    private MaterialJsonType id;
    private int count;
    
    public InitialResourceTypeIdResourceData toModel() {
        return new InitialResourceTypeIdResourceData(ResourceType.values()[id.ordinal()], count);
    }
}
