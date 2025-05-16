package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;

public record PotionDataDTO(String id,
                            String name,
                            ItemType itemType,
                            int skin,
                            int count,
                            PotionType potionType,
                            double potionPower) {
    public static PotionDataDTO fromModel(Potion model) {
        return new PotionDataDTO(model.getId(),
                                 model.getName(),
                                 model.getType(),
                                 model.getSkin(),
                                 model.getCount(),
                                 model.getPotionType(),
                                 model.getPotionPower());
    }
}
