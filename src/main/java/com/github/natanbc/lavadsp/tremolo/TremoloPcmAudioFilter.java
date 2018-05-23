package com.github.natanbc.lavadsp.tremolo;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.TremoloConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class TremoloPcmAudioFilter extends ConverterPcmAudioFilter<TremoloConverter> {
    private volatile double frequency = 2f;
    private volatile double depth = 0.5f;

    public TremoloPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        super(new TremoloConverter(sampleRate), downstream, channelCount);
        setDepth(0.5);
        setFrequency(4);
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        getConverter().setFrequency(frequency);
        this.frequency = frequency;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        getConverter().setDepth(depth);
        this.depth = depth;
    }
}