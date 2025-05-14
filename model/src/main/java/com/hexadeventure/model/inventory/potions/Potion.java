package com.hexadeventure.model.inventory.potions;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Potion extends Item {
    private PotionType potionType;
    private double potionPower;
    
    public Potion(String name, int skin, PotionType potionType, double potionPower) {
        super(name, ItemType.POTION, skin);
        this.potionType = potionType;
        this.potionPower = potionPower;
        setId(name);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Potion potion)) return false;
        if(!super.equals(o)) return false;
        return Double.compare(potionPower, potion.potionPower) == 0 && potionType == potion.potionType;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), potionPower, potionType);
    }
}
