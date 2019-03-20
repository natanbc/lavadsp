package com.github.natanbc.lavadsp.volume;

import com.github.natanbc.lavadsp.Converter;

public class VolumeConverter implements Converter {
    private float volume = 1;
    
    public void setVolume(float volume) {
        if(volume <= 0) {
            throw new IllegalArgumentException("Volume <= 0.0");
        }
        if(volume > 5) {
            throw new IllegalArgumentException("Volume > 5.0");
        }
        this.volume = volume;
    }
    
    @Override
    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        for(int i = 0; i < samples; i++) {
            output[outputOffset + i] = Math.max(-1f, Math.min(1f, input[inputOffset + i] * volume));
        }
    }
}
