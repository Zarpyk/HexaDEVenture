package com.hexadeventure.adapter.out.settings.json;

import lombok.Getter;

@Getter
public abstract class ItemJson<T> {
    private String name;
    private int skin;
    
    public abstract T toModel();
}
