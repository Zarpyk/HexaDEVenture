package com.hexadeventure.adapter.out.noise;

import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JNoiseAdapter implements NoiseGenerator {
    private final Map<String, JNoise> jNoise = new HashMap<>();
    
    public void initNoise(String id, long seed, double scale, int octaves,
                          double gain, double lacunarity, int fractalFunction, boolean incrementSeed,
                          boolean invert) {
        if(jNoise.containsKey(id)) return;
        
        FractalFunction fractal = switch (fractalFunction) {
            case FRACTAL_FBM -> FractalFunction.FBM;
            case FRACTAL_RIDGED -> FractalFunction.RIDGED_MULTI;
            case FRACTAL_TURBULENCE -> FractalFunction.TURBULENCE;
            default -> throw new IllegalArgumentException("Invalid fractal function");
        };
        
        JNoise.JNoiseBuilder<?> jNoiseBuilder = JNoise.newBuilder()
                                                      .perlin(seed, Interpolation.COSINE, FadeFunction.QUINTIC_POLY)
                                                      .scale(scale)
                                                      .octavate(octaves, gain, lacunarity, fractal, incrementSeed);
        if(invert) jNoiseBuilder.invert();
        jNoise.put(id, jNoiseBuilder.build());
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
