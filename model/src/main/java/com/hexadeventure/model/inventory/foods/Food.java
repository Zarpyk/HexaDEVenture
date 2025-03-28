package com.hexadeventure.model.inventory.foods;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Food extends Item {
    private double healthPoints;
    
    public Food(String name, int skin) {
        super(name, ItemType.FOOD, skin);
        setId(Integer.toString(hashCode()));
    }
    
    public Food(String name, int skin, double healthPoints) {
        super(name, ItemType.FOOD, skin);
        this.healthPoints = healthPoints;
        setId(Integer.toString(hashCode()));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), healthPoints);
    }
    
    @Override
    public String toString() {
        return super.toString() + " - " + hashCode();
    }
}
