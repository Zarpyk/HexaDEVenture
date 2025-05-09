package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CharacterDataDTO;
import com.hexadeventure.model.inventory.Inventory;

import java.util.List;

public record InventoryDTO(List<ItemDTO> items,
                           List<CharacterDataDTO> characters) {
    public static InventoryDTO fromModel(Inventory inventory) {
        List<ItemDTO> itemDTOs = inventory.getItems().values().stream()
                                          .map(ItemDTO::fromModel)
                                          .toList();
        List<CharacterDataDTO> characterDTOs = inventory.getCharacters().values().stream()
                                                        .map(CharacterDataDTO::fromModel)
                                                        .toList();
        return new InventoryDTO(itemDTOs, characterDTOs);
    }
}
