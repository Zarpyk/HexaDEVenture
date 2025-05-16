package com.hexadeventure.adapter.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.hexadeventure.model.map.Vector2C;

public class Vector2CDeserializer extends KeyDeserializer {
    @Override
    public Vector2C deserializeKey(String key, DeserializationContext ctxt) {
        // Remove the first and last parentheses and split the string
        String[] split = key.substring(1, key.length() - 1).split(",");
        return new Vector2C(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
