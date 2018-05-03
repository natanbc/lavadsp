package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class TremoloConverter extends NativeResourceHolder implements Converter {
    private final long instance;

    public TremoloConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        TremoloNativeLibLoader.loadTremoloLibrary();
        this.instance = TremoloLibrary.create(sampleRate);
    }

    public void setDepth(double depth) {
        checkNotReleased();

        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }

        TremoloLibrary.setDepth(instance, depth);
    }

    public void setFrequency(double frequency) {
        checkNotReleased();

        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        TremoloLibrary.setFrequency(instance, frequency);
    }

    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        checkNotReleased();

        TremoloLibrary.process(instance, input, inputOffset, output, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        TremoloLibrary.destroy(instance);
    }
}
