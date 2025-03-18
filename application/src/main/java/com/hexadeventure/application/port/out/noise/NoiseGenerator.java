package com.hexadeventure.application.port.out.noise;

public interface NoiseGenerator {
    /**
     * Initializes the noise generator with a specific ID and seed.
     *
     * @param id the identifier for the noise generator
     * @param seed the seed value for noise generation
     */
    void initNoise(String id, long seed);
    
    /**
     * Generates Perlin noise with the given ID noise generator based on the given coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param id the identifier for the noise generator
     * @return the generated Perlin noise value
     */
    double getPerlinNoise(double x, double y, String id);
    
    /**
     * Removes the noise generator associated with the specified ID.
     *
     * @param id the identifier for the noise generator to be removed
     */
    void releaseNoise(String id);
}
