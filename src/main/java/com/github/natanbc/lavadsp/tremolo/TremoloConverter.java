package com.github.natanbc.lavadsp.tremolo;

import com.github.natanbc.lavadsp.Converter;

public class TremoloConverter implements Converter {
    private final int sampleRate;
    private float frequency;
    private float depth;
    private float phase;
    
    public TremoloConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        this.sampleRate = sampleRate;
    }
    
    public void setFrequency(float frequency) {
        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        this.frequency = frequency;
    }
    
    public void setDepth(float depth) {
        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }
        //we want (0, 0.5]
        this.depth = depth / 2;
    }
    
    @Override
    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        for(int i = 0; i < samples; i++) {
            float offset = 1.0f - depth;
            float modSignal = offset + depth * (float)Math.sin(phase);
            phase += 2 * Math.PI / sampleRate * frequency;
            output[outputOffset + i] = (modSignal * input[inputOffset + i]);
        }
    }
}
