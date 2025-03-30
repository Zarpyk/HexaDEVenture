package com.hexadeventure.adapter.out.settings.json.food;

import com.hexadeventure.adapter.out.settings.json.ItemJson;
import com.hexadeventure.model.inventory.foods.Food;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodJson extends ItemJson<Food> {
    private double healthPoints;
    
    public Food toModel() {
        return new Food(getName(), getSkin(), getHealthPoints());
    }
}
