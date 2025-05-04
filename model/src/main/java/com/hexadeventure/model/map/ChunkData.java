package com.hexadeventure.model.map;

import java.util.Map;

public record ChunkData(Map<Vector2C, Chunk> chunks) {}
