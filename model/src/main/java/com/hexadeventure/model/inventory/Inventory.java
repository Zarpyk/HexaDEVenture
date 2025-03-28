package com.hexadeventure.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Inventory {
    private String id;
    private final Map<String, Item> items;
    
    public Inventory() {
        id = UUID.randomUUID().toString();
        this.items = new HashMap<>();
    }
    
    public void addItem(Item item) {
        Item itemObj = items.get(item.getId());
        if(itemObj != null) {
            itemObj.setCount(itemObj.getCount() + 1);
        } else {
            item.setCount(1);
            items.put(item.getId(), item);
        }
    }
    
    public void removeItem(Item item) {
        Item itemObj = items.get(item.getId());
        if(itemObj != null) {
            int count = itemObj.getCount() - 1;
            if(count <= 0) items.remove(item.getId());
            else itemObj.setCount(count);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Inventory inventory)) return false;
        return Objects.equals(id, inventory.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

