package com.github.natanbc.timescale.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class TimescaleConverter extends NativeResourceHolder {
    private final int[] buffer = new int[1];
    private final TimescaleLibrary library;
    private final long instance;

    public TimescaleConverter(int channels, int sampleRate, double speedRate) {
        if(channels < 1) {
            throw new IllegalArgumentException("Channels < 1");
        }
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        if(speedRate <= 0) {
            throw new IllegalArgumentException("Speed rate <= 0");
        }
        this.library = TimescaleLibrary.getInstance();
        this.instance = library.create(channels, sampleRate, speedRate);
    }

    public void reset() {
        checkNotReleased();

        library.reset(instance);
    }

    public int process(float[] input, int inputOffset, int inputLength, float[] output, int outputOffset, int outputLength) {
        checkNotReleased();

        int error = library.process(instance, input, inputOffset, inputLength, output, outputOffset, outputLength, buffer);
        if(error != 0) {
            throw new IllegalStateException("Library returned code " + error);
        }
        return buffer[0];
    }

    public int read(float[] output, int outputOffset, int outputLength) {
        checkNotReleased();

        return library.read(instance, output, outputOffset, outputLength);
    }

    public void setSpeed(double speed) {
        checkNotReleased();

        library.setSpeed(instance, speed);
    }

    public void setPitch(double pitch) {
        checkNotReleased();

        library.setPitch(instance, pitch);
    }

    @Override
    protected void freeResources() {
        library.destroy(instance);
    }
}
