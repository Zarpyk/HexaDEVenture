package com.hexadeventure.model.inventory.potions;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Potion extends Item {
    private double potionPower;
    private PotionType potionType;
    
    public Potion(String name, PotionType potionType, int skin) {
        super(name, ItemType.POTION, skin);
        this.potionType = potionType;
        setId(Integer.toString(hashCode()));
    }
    
    public Potion(String name, PotionType potionType, int skin, double potionPower) {
        super(name, ItemType.POTION, skin);
        this.potionType = potionType;
        this.potionPower = potionPower;
        setId(Integer.toString(hashCode()));
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + hashCode();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), potionPower, potionType);
    }
}
