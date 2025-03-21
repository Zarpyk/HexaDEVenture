package com.hexadeventure.adapter.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.hexadeventure.model.map.Vector2;

public class Vector2Deserializer extends KeyDeserializer {
    @Override
    public Vector2 deserializeKey(String key, DeserializationContext ctxt) {
        return new Vector2(Integer.parseInt(key.split(",")[0]), Integer.parseInt(key.split(",")[1]));
    }
}
