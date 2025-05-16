package com.hexadeventure.adapter.out.settings.json.initial;

import com.hexadeventure.model.inventory.initial.InitialStringIdResourceData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitialStringIDResourceJson {
    private String id;
    private int count;
    
    public InitialStringIdResourceData toModel() {
        return new InitialStringIdResourceData(id, count);
    }
}
