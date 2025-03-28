package com.hexadeventure.model.inventory;

import com.hexadeventure.model.inventory.characters.PlayableCharacter;
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
    private final Map<String, PlayableCharacter> characters;
    
    public Inventory() {
        id = UUID.randomUUID().toString();
        this.items = new HashMap<>();
        this.characters = new HashMap<>();
    }
    
    public void addItem(Item item) {
        addItem(item, 1);
    }
    
    public void addItem(Item item, int count) {
        Item itemObj = items.get(item.getId());
        if(itemObj != null) {
            itemObj.setCount(itemObj.getCount() + count);
        } else {
            item.setCount(count);
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
    
    public void addCharacter(PlayableCharacter character) {
        characters.put(character.getId(), character);
    }
    
    public void removeCharacter(PlayableCharacter playableCharacter) {
        characters.remove(playableCharacter.getId());
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

