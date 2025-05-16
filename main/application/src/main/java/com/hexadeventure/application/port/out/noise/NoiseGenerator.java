package com.hexadeventure.application.port.out.noise;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.Vector2C;

import java.util.Map;
import java.util.Set;

public interface NoiseGenerator {
    int FRACTAL_FBM = 0;
    int FRACTAL_RIDGED = 1;
    int FRACTAL_TURBULENCE = 2;
    
    
    /**
     * Initializes the noise generator with a specific ID and seed.
     *
     * @param id the identifier for the noise generator
     * @param seed the seed value for noise generation
     * @param octaves the number of octaves for octavation
     * @param gain the gain value for octavation
     * @param lacunarity the lacunarity value for octavation
     * @param fractalFunction the type of fractal function to use for octavation (FRACTAL_FBM, FRACTAL_RIDGED,
     *                        FRACTAL_TURBULENCE)
     * @param incrementSeed whether to increment the seed for each octave
     */
    void initNoise(String id, long seed, double scale, int octaves,
                   double gain, double lacunarity, int fractalFunction, boolean incrementSeed,
                   boolean invert);
    
    /**
     * Generates Perlin noise with the given ID noise generator based on the given coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param id the identifier for the noise generator
     * @param normalized whether to normalize the noise value
     * @return the generated Perlin noise value
     */
    double getPerlinNoise(double x, double y, String id, boolean normalized);
    
    /**
     * Removes the noise generator associated with the specified ID.
     *
     * @param id the identifier for the noise generator to be removed
     */
    void releaseNoise(String id);
    
    /**
     * Generates a circle with a noisy edge.
     *
     * @param radius the radius of the circle
     * @param seed the seed value for noise generation
     * @param chunksToGenerate the set of chunks to generate
     *
     * @return the generated circle with a noisy edge
     */
    Map<Vector2, Double> getCircleWithNoisyEdge(int radius, Vector2 center, long seed, int variation,
                                                Set<Vector2C> chunksToGenerate);
}
