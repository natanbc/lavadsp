package com.github.natanbc.lavadsp;

import com.github.natanbc.lavadsp.natives.Converter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

@SuppressWarnings("WeakerAccess")
public class ConverterPcmAudioFilter<T extends Converter> implements FloatPcmAudioFilter {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private final T converter;
    private final FloatPcmAudioFilter downstream;
    private final float[][] outputSegments;
    private final int bufferSize;

    public ConverterPcmAudioFilter(T converter, FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        this.converter = converter;
        this.downstream = downstream;
        this.bufferSize = bufferSize;
        if(bufferSize < 1) {
            this.outputSegments = null;
        } else {
            this.outputSegments = new float[channelCount][];
            for(int i = 0; i < channelCount; i++) {
                outputSegments[i] = new float[bufferSize];
            }
        }
    }

    public ConverterPcmAudioFilter(T converter, FloatPcmAudioFilter downstream, int channelCount) {
        this(converter, downstream, channelCount, DEFAULT_BUFFER_SIZE);
    }

    public T getConverter() {
        return converter;
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        if(outputSegments == null) {
            for(float[] f : input) {
                converter.process(f, offset, f, 0, length);
            }
            downstream.process(input, 0, length);
        } else {
            while(length > 0) {
                int size = Math.min(length, bufferSize);
                for(int i = 0; i < input.length; i++) {
                    converter.process(input[i], offset, outputSegments[i], 0, size);
                }
                downstream.process(outputSegments, 0, size);
                length -= bufferSize;
            }
        }
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {}

    @Override
    public void flush() {}

    @Override
    public void close() {
        converter.close();
    }

    @Deprecated
    @Override
    protected void finalize() throws Throwable {
        converter.close();
        super.finalize();
    }
}
