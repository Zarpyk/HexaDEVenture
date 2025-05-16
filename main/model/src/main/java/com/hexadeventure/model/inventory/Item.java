package com.hexadeventure.model.inventory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@NoArgsConstructor
public abstract class Item implements Comparable<Item> {
    private String id;
    private String name;
    private ItemType type;
    private int skin;
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
        if(!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id) &&
               Objects.equals(name, item.name) &&
               type == item.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
    
    @Override
    public int compareTo(Item o) {
        // Sort by name alphabetically
        int compare = this.name.compareTo(o.name);
        if(compare != 0) return compare;
        // If names are equal, compare by type
        compare = this.type.compareTo(o.type);
        if(compare != 0) return compare;
        // If types are equal, compare by skin
        compare = Integer.compare(this.skin, o.skin);
        if(compare != 0) return compare;
        // If skins are equal, compare by id
        return this.id.compareTo(o.id);
    }
}

