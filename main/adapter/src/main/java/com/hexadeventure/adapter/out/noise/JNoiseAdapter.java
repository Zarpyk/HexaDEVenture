package com.hexadeventure.adapter.out.noise;

import com.hexadeventure.adapter.utils.DoubleMapper;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;
import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JNoiseAdapter implements NoiseGenerator {
    private final Map<String, JNoise> jNoise = new HashMap<>();
    private static final String CIRCLE_NOISE_ID = "circle";
    
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
    
    public double getPerlinNoise(double x, double y, String id, boolean normalized) {
        if(!jNoise.containsKey(id)) throw new IllegalArgumentException("Noise not found");
        JNoise jNoise = this.jNoise.get(id);
        double noise = jNoise.evaluateNoise(x, y);
        if(normalized) noise = DoubleMapper.map(noise, -1, 1, 0, 1);
        return noise;
    }
    
    public void releaseNoise(String id) {
        jNoise.remove(id);
    }
    
    public Map<Vector2, Double> getCircleWithNoisyEdge(int radius, Vector2 center, long seed, int variation,
                                                       Set<Vector2C> chunksToGenerate) {
        Map<Vector2, Double> circleGradient = getCircleGradient(radius, center, false, chunksToGenerate);
        Map<Vector2, Double> circle = new HashMap<>();
        
        double maxGrad = 0.0;
        String noiseId = CIRCLE_NOISE_ID + variation;
        initNoise(noiseId, seed + 1, 0.1,
                  4, 0.2, 1.5,
                  NoiseGenerator.FRACTAL_FBM, true,
                  true);
        for (Vector2 position : circleGradient.keySet()) {
            double distX = Math.abs(position.x - center.x);
            double distY = Math.abs(position.y - center.y);
            double dist = Math.sqrt(distX * distX + distY * distY);
            
            double value;
            if(dist > radius) value = 1;
            else value = circleGradient.get(position) * getPerlinNoise(position.x, position.y, noiseId, true);
            
            value *= 20;
            if(value > maxGrad) {
                maxGrad = value;
            }
            circle.put(position, value);
        }
        
        normalize(circle, maxGrad);
        return circle;
    }
    
    // Modified from python code:
    // https://medium.com/@yvanscher/playing-with-perlin-noise-generating-realistic-archipelagos-b59f004d8401
    private Map<Vector2, Double> getCircleGradient(int radius, Vector2 center,
                                                   boolean invert, Set<Vector2C> chunksToGenerate) {
        Map<Vector2, Double> circleGrad = new HashMap<>();
        
        int maxX = radius * 2;
        int maxY = radius * 2;
        double maxGrad = Math.sqrt(maxX * maxX + maxY * maxY);
        double finalMaxGrad = 0.0;
        
        for (Vector2C chunk : chunksToGenerate) {
            for (int x = chunk.getRealX(); x < chunk.getEndX(); x++) {
                for (int y = chunk.getRealY(); y < chunk.getEndY(); y++) {
                    // (y - a)^2 + (x - b)^2 = r^2
                    double distX = Math.abs(x - center.x);
                    double distY = Math.abs(y - center.y);
                    double dist = Math.sqrt(distX * distX + distY * distY);
                    circleGrad.put(new Vector2(x, y), dist);
                    
                    if(dist > maxGrad) maxGrad = dist;
                }
            }
        }
        
        for (Vector2 position : circleGrad.keySet()) {
            double value = circleGrad.get(position);
            // Get it between -1 and 1
            value /= maxGrad;
            value -= 0.5;
            value *= 2.0;
            if(invert) value = -value;
            
            // Shrink gradient
            if(value > 0) {
                value *= 20;
            }
            
            // Get it between 0 and 1
            if(value > finalMaxGrad) {
                finalMaxGrad = value;
            }
            circleGrad.put(position, value);
        }
        
        normalize(circleGrad, finalMaxGrad);
        return circleGrad;
    }
    
    private void normalize(Map<Vector2, Double> circle, double maxGrad) {
        for (Vector2 position : circle.keySet()) {
            double value = circle.get(position);
            if(value <= 0) {
                circle.put(position, 0d);
            } else {
                circle.put(position, value / maxGrad);
            }
        }
    }
}
