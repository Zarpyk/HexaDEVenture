package com.hexadeventure.adapter.in.rest.game.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record UnequipWeaponDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                       minLength = 1)
                               String characterId) {}
