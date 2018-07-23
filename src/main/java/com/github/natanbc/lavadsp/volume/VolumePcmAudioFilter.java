package com.github.natanbc.lavadsp.volume;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.VolumeConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class VolumePcmAudioFilter extends ConverterPcmAudioFilter<VolumeConverter> {
    private double volume = 1.0f;

    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        super(new VolumeConverter(), downstream, channelCount, bufferSize);
    }

    public double getVolume() {
        return volume;
    }

    public VolumePcmAudioFilter setVolume(double volume) {
        if (volume < 0.0)
            throw new IllegalArgumentException("Volume is less than 0!");

        getConverter().setVolume(volume);
        this.volume = volume;
        return this;
    }
}
