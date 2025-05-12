package com.hexadeventure.model.inventory.foods;

import com.hexadeventure.model.inventory.Item;
import com.hexadeventure.model.inventory.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Food extends Item {
    private double healthPoints;
    
    public Food(String name, int skin) {
        super(name, ItemType.FOOD, skin);
        setId(name);
    }
    
    public Food(String name, int skin, double healthPoints) {
        super(name, ItemType.FOOD, skin);
        this.healthPoints = healthPoints;
        setId(name);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Food food)) return false;
        if(!super.equals(o)) return false;
        return Double.compare(healthPoints, food.healthPoints) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), healthPoints);
    }
}
