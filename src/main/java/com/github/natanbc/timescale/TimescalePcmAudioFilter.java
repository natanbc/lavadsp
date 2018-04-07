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

    /**
     * Sets the playback speed. This calls the SoundTouch setTempo function.
     *
     * @param speed Speed to play at. 1.0 means unchanged.
     */
    public void setSpeed(double speed) {
        for(TimescaleConverter converter : converters) {
            converter.setSpeed(speed);
        }
    }

    /**
     * Sets the playback speed, in percentage, relative to the default.
     * <br>This is equivalent to {@code setSpeed(1.0 + 0.01 * change)}
     *
     * @param change Percentage relative to the default speed to play at.
     */
    public void setSpeedChange(double change) {
        setSpeed(1.0 + 0.01 * change);
    }

    /**
     * Sets the audio pitch.
     *
     * @param pitch Pitch to set. 1.0 means unchanged.
     */
    public void setPitch(double pitch) {
        for(TimescaleConverter converter : converters) {
            converter.setPitch(pitch);
        }
    }

    /**
     * Sets the audio pitch in octaves. This is equivalent to {@code setPitch(Math.exp(0.69314718056 * Math.max(Math.min(pitch, 1.0), -1.0)))}.
     *
     * @param pitch Octaves to set.
     *
     * @see #setPitch(double)
     */
    public void setPitchOctaves(double pitch) {
        setPitch(Math.exp(0.69314718056 * Math.max(Math.min(pitch, 1.0), -1.0)));
    }

    /**
     * Sets the audio pitch in semi tones. This is equivalent to {@code setPitchOctaves(pitch / 12.0)}.
     *
     * @param pitch Semi tones to set.
     *
     * @see #setPitch(double)
     * @see #setPitchOctaves(double)
     */
    public void setPitchSemiTones(double pitch) {
        setPitchOctaves(pitch / 12.0);
    }

    /**
     * Sets the audio rate, in percentage, relative to the default.
     *
     * @param rate Rate to set. 1.0 means unchanged.
     */
    public void setRate(double rate) {
        for(TimescaleConverter converter : converters) {
            converter.setRate(rate);
        }
    }

    /**
     * Sets the audio rate.
     * <br>This is equivalent to {@code setRate(1.0 + 0.01 * change)}
     *
     * @param change Percentage relative to the default rate to play at.
     *
     * @see #setRate(double)
     */
    public void setRateChange(double change) {
        setRate(1.0 + 0.01 * change);
    }

    /**
     * Queries processing sequence size in samples.
     *
     * <br>This value gives an approximate value of how many input samples you'll need to
     * feed into SoundTouch after initial buffering to get out a new batch of
     * output samples.
     * <br>
     * <br>This value does not include initial buffering at beginning of a new processing
     * stream, use {@link #getInitialLatencies()} to get the initial buffering size.
     *
     * @return Processing sequence size in samples.
     *
     * @see #getInitialLatencies()
     */
    public int[] getNominalInputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalInputSequence();
        }
        return ret;
    }

    /**
     * Queries nominal average processing output size in samples.
     *
     * <br>This value tells an approximate value of how many output samples
     * SoundTouch outputs once it does DSP processing run for a batch of input samples.
     *
     * @return Nominal average processing output size in samples.
     */
    public int[] getNominalOutputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalOutputSequence();
        }
        return ret;
    }

    /**
     * Queries initial processing latencies, i.e.
     * approx. how many samples you'll need to enter to SoundTouch pipeline before
     * you can expect to get first batch of ready output samples out.
     *
     * @return Initial processing latencies.
     */
    public int[] getInitialLatencies() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getInitialLatency();
        }
        return ret;
    }

    /**
     * Gets a setting from the converters.
     *
     * @param setting Setting to read.
     * @param <T> Type of the setting.
     *
     * @return The setting value.
     */
    public <T> T getSetting(Setting<T> setting) {
        //all converters always have the same settings
        return setting.get(converters[0]);
    }

    /**
     * Sets a converter setting.
     *
     * @param setting Setting to set.
     * @param value Value to set.
     * @param <T> Type of the setting.
     *
     * @return {@code true} if the setting was updated.
     */
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
