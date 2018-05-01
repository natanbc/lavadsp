package com.github.natanbc.lavadsp.vibrato;

import com.github.natanbc.lavadsp.natives.VibratoConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class VibratoPcmAudioFilter implements FloatPcmAudioFilter {
    private static final int BUFFER_SIZE = 4096;

    private final FloatPcmAudioFilter downstream;
    private final float[][] outputSegments;
    //since it has no buffer we only need one
    private final VibratoConverter converter;
    //values taken from BerVibrato.h
    private volatile float frequency = 2f;
    private volatile float depth = 0.5f;

    public VibratoPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        this.downstream = downstream;
        this.outputSegments = new float[channelCount][];
        for(int i = 0; i < channelCount; i++) {
            outputSegments[i] = new float[BUFFER_SIZE];
        }
        this.converter = new VibratoConverter(sampleRate);
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        converter.setFrequency(frequency);
        this.frequency = frequency;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        converter.setDepth(depth);
        this.depth = depth;
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        while(length > 0) {
            int size = Math.min(length, BUFFER_SIZE);
            for(int i = 0; i < input.length; i++) {
                converter.process(input[i], offset, outputSegments[i], 0, size);
            }
            downstream.process(outputSegments, 0, size);
            length -= BUFFER_SIZE;
        }
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {
        converter.close();
    }
}
