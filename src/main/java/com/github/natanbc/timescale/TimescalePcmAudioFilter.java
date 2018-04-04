package com.github.natanbc.timescale;

import com.github.natanbc.timescale.natives.TimescaleConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class TimescalePcmAudioFilter implements FloatPcmAudioFilter {
    private static final int BUFFER_SIZE = 4096;

    private final FloatPcmAudioFilter downstream;
    private final TimescaleConverter[] converters;
    private final float[][] outputSegments;

    /**
     * @param channels Number of channels in input data.
     * @param downstream Next filter in chain.
     * @param sampleRate Sample rate.
     * @param timeScale Time scale rate.
     */
    public TimescalePcmAudioFilter(int channels, FloatPcmAudioFilter downstream, int sampleRate, double timeScale) {
        this.downstream = downstream;
        converters = new TimescaleConverter[channels];
        outputSegments = new float[channels][];

        for (int i = 0; i < channels; i++) {
            outputSegments[i] = new float[BUFFER_SIZE];
            converters[i] = new TimescaleConverter(1, sampleRate, timeScale);
        }
    }

    public void setSpeed(double speed) {
        for(TimescaleConverter converter : converters) {
            converter.setSpeed(speed);
        }
    }

    public void setSpeedChange(double change) {
        setSpeed(1.0 + 0.01 * change);
    }

    public void setPitch(double pitch) {
        for(TimescaleConverter converter : converters) {
            converter.setPitch(pitch);
        }
    }

    public void setPitchOctaves(double pitch) {
        setPitch(Math.exp(0.69314718056 * Math.max(Math.min(pitch, 1.0), -1.0)));
    }

    public void setPitchSemiTones(double pitch) {
        setPitchOctaves(pitch / 12.0);
    }

    public void setRate(double rate) {
        for(TimescaleConverter converter : converters) {
            converter.setRate(rate);
        }
    }

    public void setRateChange(double change) {
        setRate(1.0 + 0.01 * change);
    }

    public int[] getNominalInputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalInputSequence();
        }
        return ret;
    }

    public int[] getNominalOutputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalOutputSequence();
        }
        return ret;
    }

    public int[] getInitialLatencies() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getInitialLatency();
        }
        return ret;
    }

    public <T> T getSetting(Setting<T> setting) {
        return setting.get(converters[0]);
    }

    public <T> boolean setSetting(Setting<T> setting, T value) {
        boolean success = false;
        for(TimescaleConverter converter : converters) {
            success = setting.set(converter, value);
        }
        return success;
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        for (TimescaleConverter converter : converters) {
            converter.reset();
        }
    }

    @Override
    public void flush() {
        for(TimescaleConverter converter : converters) {
            converter.flush();
        }
    }

    @Override
    public void close() {
        for (TimescaleConverter converter : converters) {
            converter.close();
        }
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        int written = 0;
        for (int i = 0; i < input.length; i++) {
            written = converters[i].process(input[i], offset, length, outputSegments[i], 0, BUFFER_SIZE);
        }

        if(written > 0) {
            downstream.process(outputSegments, 0, written);
        }

        while(true) {
            boolean allEmpty = true;
            for(int i = 0; i < input.length; i++) {
                written = converters[i].read(outputSegments[i], 0, BUFFER_SIZE);
                if(written != 0) allEmpty = false;
            }
            if(allEmpty) break;
            downstream.process(outputSegments, 0, written);

        }
    }

}
