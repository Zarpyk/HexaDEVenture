package com.hexadeventure.adapter.in.rest.game.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceCharacterDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                        minimum = "0", maximum = "3")
                                int row,
                                @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                        minimum = "0", maximum = "4")
                                int column,
                                @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                                        minLength = 1)
                                String characterId) {}
