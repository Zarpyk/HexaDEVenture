package com.hexadeventure.adapter.out.settings.json.initial;

import com.hexadeventure.model.inventory.initial.InitialResourceTypeIdResourceData;
import com.hexadeventure.model.inventory.initial.InitialResources;
import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import com.hexadeventure.model.map.resources.ResourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
public class InitialResourcesJson {
    private InitialStringIDResourceJson[] initialWeapons;
    private InitialStringIDResourceJson[] initialFood;
    private InitialStringIDResourceJson[] initialPotions;
    private InitialMaterialTypeIDResourceJson[] initialMaterials;
    
    public static ResourceType getID(com.hexadeventure.adapter.out.settings.json.material.MaterialJson materialJson) {
        return ResourceType.values()[materialJson.getMaterialType().ordinal()];
    }
    
    public InitialResources toModel() {
        InitialStringIdResourceData[] weapons = Arrays.stream(initialWeapons)
                                                      .map(InitialStringIDResourceJson::toModel)
                                                      .toArray(InitialStringIdResourceData[]::new);
        
        InitialStringIdResourceData[] foods = Arrays.stream(initialFood)
                                                    .map(InitialStringIDResourceJson::toModel)
                                                    .toArray(InitialStringIdResourceData[]::new);
        
        InitialStringIdResourceData[] potions = Arrays.stream(initialPotions)
                                                      .map(InitialStringIDResourceJson::toModel)
                                                      .toArray(InitialStringIdResourceData[]::new);
        
        InitialResourceTypeIdResourceData[] materials = Arrays.stream(initialMaterials)
                                                              .map(InitialMaterialTypeIDResourceJson::toModel)
                                                              .toArray(InitialResourceTypeIdResourceData[]::new);
        
        InitialResources initialResources = new InitialResources();
        initialResources.setInitialWeapons(weapons);
        initialResources.setInitialFoods(foods);
        initialResources.setInitialPotions(potions);
        initialResources.setInitialMaterials(materials);
        return initialResources;
    }
}
