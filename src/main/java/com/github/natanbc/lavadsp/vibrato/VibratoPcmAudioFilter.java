package com.github.natanbc.lavadsp.vibrato;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.VibratoConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class VibratoPcmAudioFilter extends ConverterPcmAudioFilter<VibratoConverter> {
    //values taken from BerVibrato.h
    private volatile float frequency = 2f;
    private volatile float depth = 0.5f;

    public VibratoPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        super(new VibratoConverter(sampleRate), downstream, channelCount);
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        getConverter().setFrequency(frequency);
        this.frequency = frequency;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        getConverter().setDepth(depth);
        this.depth = depth;
    }
}
