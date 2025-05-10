package com.hexadeventure.adapter.in.rest.game.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record StartGameDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
                           long seed,
                           @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                   minimum = "144", multipleOf = 16)
                           int size) {}
