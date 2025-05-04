package com.hexadeventure.adapter.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.hexadeventure.adapter.in.rest.game.map.Vector2DTO;

public class Vector2DTODeserializer extends KeyDeserializer {
    @Override
    public Vector2DTO deserializeKey(String key, DeserializationContext ctxt) {
        // Remove the first and last parentheses and split the string
        String[] split = key.substring(1, key.length() - 1).split(",");
        return new Vector2DTO(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
