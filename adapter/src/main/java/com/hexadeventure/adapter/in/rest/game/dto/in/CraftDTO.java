package com.hexadeventure.adapter.in.rest.game.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record CraftDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                               minimum = "0")
                       int recipeIndex,
                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                               minimum = "1")
                       int count) {
}
