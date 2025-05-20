package com.hexadeventure.adapter.out.noise;

import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JNoiseAdapterTest {
    private final JNoiseAdapter noiseAdapter = new JNoiseAdapter();
    
    @BeforeEach
    public void beforeEach() {
        noiseAdapter.initNoise("test", 1, 0.1,
                               4, 0.2, 1.5, NoiseGenerator.FRACTAL_FBM,
                               true, true);
    }
    
    @AfterEach
    public void afterEach() {
        noiseAdapter.releaseNoise("test");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {NoiseGenerator.FRACTAL_FBM, NoiseGenerator.FRACTAL_RIDGED, NoiseGenerator.FRACTAL_TURBULENCE})
    void givenParameters_whenInitNoise_thenNoiseIsInitialized(int fractal) {
        noiseAdapter.initNoise("test", 1, 0.1,
                               4, 0.2, 1.5, fractal,
                               true, true);
        assertThat(noiseAdapter.getPerlinNoise(0, 0, "test", false)).isBetween(-1.0, 1.0);
    }
    
    @Test
    public void whenGenerateNonNormalizedNoise_thenNoiseIsGenerated() {
        double noise = noiseAdapter.getPerlinNoise(0, 0, "test", false);
        assertThat(noise).isBetween(-1.0, 1.0);
    }
    
    @Test
    public void whenGenerateNormalizedNoise_thenNoiseIsGenerated() {
        double noise = noiseAdapter.getPerlinNoise(0, 0, "test", true);
        assertThat(noise).isBetween(0.0, 1.0);
    }
    
    @Test
    public void givenNonExistentNoiseId_whenGetPerlinNoise_thenExceptionIsThrown() {
        noiseAdapter.releaseNoise("test");
        assertThatThrownBy(() -> noiseAdapter.getPerlinNoise(0, 0, "test", false))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void givenInvertParameter_whenInitNoise_thenNoiseIsInverted() {
        double normalNoise = noiseAdapter.getPerlinNoise(0, 0, "test", false);
        noiseAdapter.releaseNoise("test");
        noiseAdapter.initNoise("test", 1, 0.1,
                               4, 0.2, 1.5, NoiseGenerator.FRACTAL_FBM,
                               true, true);
        double noise = noiseAdapter.getPerlinNoise(0, 0, "test", false);
        assertThat(noise).isEqualTo(normalNoise * -1);
    }
}
