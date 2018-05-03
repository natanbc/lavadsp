package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class VibratoConverter extends NativeResourceHolder implements Converter {
    private final long instance;

    public VibratoConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        VibratoNativeLibLoader.loadVibratoLibrary();
        this.instance = VibratoLibrary.create(sampleRate);
    }

    public void setDepth(float depth) {
        checkNotReleased();

        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }

        VibratoLibrary.setDepth(instance, depth);
    }

    public void setFrequency(float frequency) {
        checkNotReleased();

        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        if(frequency > VibratoNativeLibLoader.maxFrequency()) {
            throw new IllegalArgumentException("Frequency > max (" + VibratoNativeLibLoader.maxFrequency() + ")");
        }
        VibratoLibrary.setFrequency(instance, frequency);
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
