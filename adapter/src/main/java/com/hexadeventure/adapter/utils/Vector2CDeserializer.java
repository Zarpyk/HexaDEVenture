package com.hexadeventure.adapter.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.hexadeventure.model.map.Vector2C;

public class Vector2CDeserializer extends KeyDeserializer {
    @Override
    public Vector2C deserializeKey(String key, DeserializationContext ctxt) {
        return new Vector2C(Integer.parseInt(key.split(",")[0]), Integer.parseInt(key.split(",")[1]));
    }
}
