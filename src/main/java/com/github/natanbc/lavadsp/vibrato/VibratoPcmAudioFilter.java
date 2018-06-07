package com.github.natanbc.lavadsp.vibrato;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.VibratoConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * <a href="https://en.wikipedia.org/wiki/Vibrato">Vibrato</a> filter implementation.
 */
public class VibratoPcmAudioFilter extends ConverterPcmAudioFilter<VibratoConverter> {
    //values taken from BerVibrato.h
    private volatile float frequency = 2f;
    private volatile float depth = 0.5f;

    public VibratoPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        super(new VibratoConverter(sampleRate), downstream, channelCount);
    }

    /**
     * Returns the effect frequency.
     *
     * @return The effect frequency.
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * Sets the effect frequency.
     *
     * @param frequency Frequency to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public VibratoPcmAudioFilter setFrequency(float frequency) {
        getConverter().setFrequency(frequency);
        this.frequency = frequency;
        return this;
    }

    /**
     * Returns the effect depth.
     *
     * @return The effect depth.
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the effect depth.
     *
     * @param depth Depth to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public VibratoPcmAudioFilter setDepth(float depth) {
        getConverter().setDepth(depth);
        this.depth = depth;
        return this;
    }
}
