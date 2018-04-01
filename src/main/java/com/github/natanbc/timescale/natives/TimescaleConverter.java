package com.github.natanbc.timescale.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class TimescaleConverter extends NativeResourceHolder {
    private final int[] buffer = new int[1];
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
        TimescaleNativeLibLoader.loadTimescaleLibrary();
        this.instance = TimescaleLibrary.create(channels, sampleRate, speedRate);
    }

    public void reset() {
        checkNotReleased();

        TimescaleLibrary.reset(instance);
    }

    public int process(float[] input, int inputOffset, int inputLength, float[] output, int outputOffset, int outputLength) {
        checkNotReleased();

        int error = TimescaleLibrary.process(instance, input, inputOffset, inputLength, output, outputOffset, outputLength, buffer);
        if(error != 0) {
            throw new IllegalStateException("Library returned code " + error);
        }
        return buffer[0];
    }

    public int read(float[] output, int outputOffset, int outputLength) {
        checkNotReleased();

        return TimescaleLibrary.read(instance, output, outputOffset, outputLength);
    }

    public void setSpeed(double speed) {
        checkNotReleased();

        TimescaleLibrary.setSpeed(instance, speed);
    }

    public void setPitch(double pitch) {
        checkNotReleased();

        TimescaleLibrary.setPitch(instance, pitch);
    }

    public int getNominalInputSequence() {
        return getSetting(6);
    }

    public int getNominalOutputSequence() {
        return getSetting(7);
    }

    public int getInitialLatency() {
        return getSetting(8);
    }

    int getSetting(int setting) {
        checkNotReleased();

        return TimescaleLibrary.getSetting(instance, setting);
    }

    boolean setSetting(int setting, int value) {
        checkNotReleased();

        return TimescaleLibrary.setSetting(instance, setting, value);
    }

    @Override
    protected void freeResources() {
        TimescaleLibrary.destroy(instance);
    }
}
