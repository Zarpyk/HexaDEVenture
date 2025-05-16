package com.hexadeventure.adapter.out.settings.json.initial;

import com.hexadeventure.model.inventory.initial.InitialCharacter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitialCharacterJson {
    private String name;
    private int minHealth;
    private int maxHealth;
    private int minSpeed;
    private int maxSpeed;
    
    public static InitialCharacter toModel(InitialCharacterJson json) {
        return new InitialCharacter(json.name,
                                    json.minHealth,
                                    json.maxHealth,
                                    json.minSpeed,
                                    json.maxSpeed);
    }
}
