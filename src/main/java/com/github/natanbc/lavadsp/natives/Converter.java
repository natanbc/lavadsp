package com.github.natanbc.lavadsp.natives;

public interface Converter extends AutoCloseable {
    @Override
    void close();

    void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples);


}
