package com.hexadeventure.adapter.in.rest.game.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record EquipWeaponDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                     minLength = 1)
                             String characterId,
                             @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                     minLength = 1)
                             String weaponId) {}
