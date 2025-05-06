package com.hexadeventure.model.inventory.characters;


import com.hexadeventure.model.inventory.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loot {
    private ItemType type;
    private String id;
    private int count;
}
