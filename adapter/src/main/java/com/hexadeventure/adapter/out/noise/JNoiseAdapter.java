package com.hexadeventure.adapter.out.noise;

import com.hexadeventure.adapter.utils.DoubleMapper;
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
    
    public double[][] getCircleWithNoisyEdge(int radius, long seed, int variation) {
        double[][] circleGradient = getCircleGradient(radius, false);
        double[][] circle = new double[radius * 2][radius * 2];
        
        double maxGrad = 0.0;
        String noiseId = CIRCLE_NOISE_ID + variation;
        initNoise(noiseId, seed + 1, 0.1,
                  4, 0.2, 1.5,
                  NoiseGenerator.FRACTAL_FBM, true,
                  true);
        for (int y = 0; y < circle.length; y++) {
            for (int x = 0; x < circle.length; x++) {
                double distX = Math.abs(x - radius);
                double distY = Math.abs(y - radius);
                double dist = Math.sqrt(distX * distX + distY * distY);
                if(dist > radius) circle[y][x] = 1;
                else circle[y][x] = circleGradient[y][x] * getPerlinNoise(x, y, noiseId, true);
                circle[y][x] *= 20;
                if(circle[y][x] > maxGrad) {
                    maxGrad = circle[y][x];
                }
            }
        }
        
        return Normalize(circle, maxGrad);
    }
    
    // Modified from python code:
    // https://medium.com/@yvanscher/playing-with-perlin-noise-generating-realistic-archipelagos-b59f004d8401
    private double[][] getCircleGradient(int radius, boolean invert) {
        double[][] circleGrad = new double[radius * 2][radius * 2];
        
        double maxGrad = 0.0;
        double finalMaxGrad = 0.0;
        
        for (int y = 0; y < circleGrad.length; y++) {
            for (int x = 0; x < circleGrad.length; x++) {
                // (x - a)^2 + (y - b)^2 = r^2
                double distX = Math.abs(x - radius);
                double distY = Math.abs(y - radius);
                double dist = Math.sqrt(distX * distX + distY * distY);
                circleGrad[y][x] = dist;
                
                if(dist > maxGrad) maxGrad = dist;
            }
        }
        
        for (int y = 0; y < circleGrad.length; y++) {
            for (int x = 0; x < circleGrad.length; x++) {
                // Get it between -1 and 1
                circleGrad[y][x] /= maxGrad;
                circleGrad[y][x] -= 0.5;
                circleGrad[y][x] *= 2.0;
                if(invert) circleGrad[y][x] = -circleGrad[y][x];
                
                // Shrink gradient
                if(circleGrad[y][x] > 0) {
                    circleGrad[y][x] *= 20;
                }
                
                // Get it between 0 and 1
                if(circleGrad[y][x] > finalMaxGrad) {
                    finalMaxGrad = circleGrad[y][x];
                }
            }
        }
        
        for (int y = 0; y < circleGrad.length; y++) {
            for (int x = 0; x < circleGrad.length; x++) {
                double distX = Math.abs(x - radius);
                double distY = Math.abs(y - radius);
                double dist = Math.sqrt(distX * distX + distY * distY);
                if(dist > radius) {
                    circleGrad[y][x] = finalMaxGrad;
                }
            }
        }
        
        return Normalize(circleGrad, finalMaxGrad);
    }
    
    private double[][] Normalize(double[][] circle, double maxGrad) {
        for (int y = 0; y < circle.length; y++) {
            for (int x = 0; x < circle.length; x++) {
                if(circle[y][x] <= 0) {
                    circle[y][x] = 0;
                } else {
                    circle[y][x] /= maxGrad;
                }
            }
        }
        
        return circle;
    }
}
