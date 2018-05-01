package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class TimescaleConverter extends NativeResourceHolder {
    private final int[] buffer = new int[1];
    private final long instance;

    public TimescaleConverter(int channels, int sampleRate) {
        if(channels < 1) {
            throw new IllegalArgumentException("Channels < 1");
        }
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        TimescaleNativeLibLoader.loadTimescaleLibrary();
        this.instance = TimescaleLibrary.create(channels, sampleRate);
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

        if(speed <= 0) {
            throw new IllegalArgumentException("speed <= 0");
        }

        TimescaleLibrary.setSpeed(instance, speed);
    }

    public void setPitch(double pitch) {
        checkNotReleased();

        if(pitch <= 0) {
            throw new IllegalArgumentException("pitch <= 0");
        }

        TimescaleLibrary.setPitch(instance, pitch);
    }

    public void setRate(double rate) {
        checkNotReleased();

        if(rate <= 0) {
            throw new IllegalArgumentException("rate <= 0");
        }

        TimescaleLibrary.setRate(instance, rate);
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

    public int getSetting(int setting) {
        checkNotReleased();

        return TimescaleLibrary.getSetting(instance, setting);
    }

    public boolean setSetting(int setting, int value) {
        checkNotReleased();

        return TimescaleLibrary.setSetting(instance, setting, value);
    }

    public double getInputOutputSampleRatio() {
        checkNotReleased();

        return TimescaleLibrary.getInputOutputSampleRatio(instance);
    }

    public void flush() {
        checkNotReleased();

        TimescaleLibrary.flush(instance);
    }

    public int numUnprocessedSamples() {
        checkNotReleased();

        return TimescaleLibrary.numUnprocessedSamples(instance);
    }

    public int numSamples() {
        checkNotReleased();

        return TimescaleLibrary.numSamples(instance);
    }

    public boolean isEmpty() {
        checkNotReleased();

        return TimescaleLibrary.isEmpty(instance);
    }

    @Override
    protected void freeResources() {
        TimescaleLibrary.destroy(instance);
    }
}
