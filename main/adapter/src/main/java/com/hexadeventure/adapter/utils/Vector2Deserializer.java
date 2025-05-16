package com.hexadeventure.adapter.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.hexadeventure.model.map.Vector2;

public class Vector2Deserializer extends KeyDeserializer {
    @Override
    public Vector2 deserializeKey(String key, DeserializationContext ctxt) {
        // Remove the first and last parentheses and split the string
        String[] split = key.substring(1, key.length() - 1).split(",");
        return new Vector2(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
