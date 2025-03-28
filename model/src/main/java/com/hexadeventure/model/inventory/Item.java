package com.hexadeventure.model.inventory;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public abstract class Item {
    private String id;
    private final String name;
    private final ItemType type;
    private final int skin;
    @Setter
    private int count;
    
    protected Item(String name, ItemType type, int skin) {
        this.name = name;
        this.type = type;
        this.skin = skin;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return name + "-" + skin + "-" + type;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type, skin);
    }
}

