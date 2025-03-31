package com.hexadeventure.model.inventory.initial;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitialResources {
    private InitialStringIdResourceData[] initialWeapons;
    private InitialStringIdResourceData[] initialFoods;
    private InitialStringIdResourceData[] initialPotions;
    private InitialResourceTypeIdResourceData[] initialMaterials;
}
