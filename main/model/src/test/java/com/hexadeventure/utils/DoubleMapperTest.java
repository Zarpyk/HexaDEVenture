package com.hexadeventure.utils;

import com.hexadeventure.model.utils.DoubleMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleMapperTest {
    @ParameterizedTest(name = "value={0} -> expected={1}")
    @CsvSource(value = {
            "0, 0",
            "1, 100",
            "0.1, 10",
            "0.9, 90"
    })
    public void givenValueBetween0And1_whenMapTo0To100_thenReturnMappedValue(double value, double expected) {
        double result = DoubleMapper.map(value, 0, 1, 0, 100);
        
        assertThat(result).isEqualTo(expected);
    }
}
