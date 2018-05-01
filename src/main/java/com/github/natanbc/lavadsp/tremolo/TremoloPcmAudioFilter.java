package com.github.natanbc.lavadsp.tremolo;

import com.github.natanbc.lavadsp.natives.TremoloConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class TremoloPcmAudioFilter implements FloatPcmAudioFilter {
    private static final int BUFFER_SIZE = 4096;

    private final FloatPcmAudioFilter downstream;
    private final float[][] outputSegments;
    //since it has no buffer we only need one
    private final TremoloConverter converter;
    private volatile double frequency = 2f;
    private volatile double depth = 0.5f;

    public TremoloPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        this.downstream = downstream;
        this.outputSegments = new float[channelCount][];
        for(int i = 0; i < channelCount; i++) {
            outputSegments[i] = new float[BUFFER_SIZE];
        }
        this.converter = new TremoloConverter(sampleRate);
        setDepth(0.5);
        setFrequency(4);
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        converter.setFrequency(frequency);
        this.frequency = frequency;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
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