package com.hexadeventure.adapter.utils;

public class DoubleMapper {
    /**
     * Maps a value from one range to another.
     *
     * @see
     * <a href="https://stackoverflow.com/a/5735770/11451105">Mapping a numeric range onto another</a>
     */
    public static double map(double value, double in_min, double in_max, double out_min, double out_max) {
        return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
