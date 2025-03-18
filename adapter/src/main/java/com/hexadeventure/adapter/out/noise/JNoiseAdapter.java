package com.hexadeventure.adapter.out.noise;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import org.springframework.stereotype.Component;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;

import java.util.HashMap;
import java.util.Map;

@Component
public class JNoiseAdapter implements NoiseGenerator {
    private final Map<String, JNoise> jNoise = new HashMap<>();
    
    public void initNoise(String id, long seed) {
        if(jNoise.containsKey(id)) return;
        jNoise.put(id, JNoise.newBuilder()
                             .perlin(seed, Interpolation.COSINE, FadeFunction.QUINTIC_POLY)
                             .scale(0.1)
                             .abs()
                             .build());
    }
    
    public double getPerlinNoise(double x, double y, String id) {
        if(!jNoise.containsKey(id)) throw new IllegalArgumentException("Noise not found");
        JNoise jNoise = this.jNoise.get(id);
        return jNoise.evaluateNoise(x, y);
    }
    
    public void releaseNoise(String id) {
        jNoise.remove(id);
    }
}
