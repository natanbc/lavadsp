package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class VibratoConverter extends NativeResourceHolder {
    private final long instance;

    public VibratoConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        VibratoNativeLibLoader.loadVibratoLibrary();
        this.instance = VibratoLibrary.initialize(sampleRate);
    }

    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        checkNotReleased();

        VibratoLibrary.process(instance, input, inputOffset, output, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        VibratoLibrary.destroy(instance);
    }
}
