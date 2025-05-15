package com.hexadeventure.adapter.in.rest.game.dto.out.inventory;

import com.hexadeventure.model.inventory.Inventory;

import java.util.List;

public record InventoryDTO(List<ItemDTO> items,
                           List<CharacterDTO> characters) {
    public static InventoryDTO fromModel(Inventory inventory) {
        List<ItemDTO> itemDTOs = inventory.getItems().values().stream().sorted()
                                          .map(ItemDTO::fromModel)
                                          .toList();
        List<CharacterDTO> characterDTOs = inventory.getCharacters().values().stream().sorted()
                                                    .map(CharacterDTO::fromModel)
                                                    .toList();
        return new InventoryDTO(itemDTOs, characterDTOs);
    }
}
