package com.hexadeventure.adapter.out.settings.json.enemy;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.characters.Loot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LootJson {
    private ItemType type;
    private String id;
    private int count;
    
    public Loot toModel() {
        return new Loot(type, id, count);
    }
}