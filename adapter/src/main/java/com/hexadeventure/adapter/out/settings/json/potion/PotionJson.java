package com.hexadeventure.adapter.out.settings.json.potion;

import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PotionJson extends ItemJson<Potion> {
    private double potionPower;
    private PotionType potionType;
    
    public static String getID(PotionJson potionJson) {
        return potionJson.getName();
    }
    
    public Potion toModel() {
        return new Potion(getName(), getSkin(), potionPower, potionType);
    }
}
