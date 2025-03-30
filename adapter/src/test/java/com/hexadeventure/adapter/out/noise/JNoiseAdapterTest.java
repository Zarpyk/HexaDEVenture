package com.hexadeventure.adapter.out.noise;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class JNoiseAdapterTest {
    private final JNoiseAdapter noiseAdapter = new JNoiseAdapter();
    
    @BeforeEach
    public void beforeEach() {
        noiseAdapter.initNoise("test", 1, 0.1,
                               4, 0.2, 1.5, 0,
                               true, true);
    }
    
    @AfterEach
    public void afterEach() {
        noiseAdapter.releaseNoise("test");
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
}
