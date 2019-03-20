package com.github.natanbc.lavadsp.vibrato;

import com.github.natanbc.lavadsp.Converter;

import java.nio.FloatBuffer;

public class VibratoConverter implements Converter {
    private static final int ADDITIONAL_DELAY = 3;
    private static final float BASE_DELAY_SEC = 0.002f; // 2 ms
    private static final float VIBRATO_FREQUENCY_DEFAULT_HZ = 2;
    private static final float VIBRATO_FREQUENCY_MAX_HZ = 14;
    private static final float VIBRATO_DEPTH_DEFAULT_PERCENT = 50;
    
    private final float sampleRate;
    private final Lfo lfo;
    private final RingBuffer buffer;
    private float depth = VIBRATO_DEPTH_DEFAULT_PERCENT / 100;
    
    public VibratoConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        this.sampleRate = sampleRate;
        this.lfo = new Lfo(sampleRate);
        this.buffer = new RingBuffer((int)(BASE_DELAY_SEC * sampleRate * 2));
    }
    
    public void setDepth(float depth) {
        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }
        this.depth = depth;
    }
    
    public void setFrequency(float frequency) {
        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        if(frequency > VIBRATO_FREQUENCY_MAX_HZ) {
            throw new IllegalArgumentException("Frequency > max (" + VIBRATO_FREQUENCY_MAX_HZ + ")");
        }
        lfo.frequency = frequency;
    }
    
    @Override
    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        for(int i = 0; i < samples; i++) {
            float lfoValue = lfo.getValue();
            float maxDelay = BASE_DELAY_SEC * sampleRate;
    
            float delay = lfoValue * depth * maxDelay + ADDITIONAL_DELAY;
    
            output[outputOffset + i] = buffer.getHermiteAt(delay);
    
            buffer.writeMargined(input[inputOffset + i]);
        }
    }
    
    private static class Lfo {
        private final float sampleRate;
        float frequency = VIBRATO_FREQUENCY_DEFAULT_HZ;
        float phase;
    
        Lfo(float sampleRate) {
            this.sampleRate = sampleRate;
        }
        
        float getValue() {
            float dp = (float)(2 * Math.PI * frequency / sampleRate); // phase step
    
            float value = (float)Math.sin(phase);
            value = (value + 1f) * 0.5f; // transform from [-1; 1] to [0; 1]
    
            phase += dp;
            while(phase > 2 * Math.PI)
                phase -= 2 * Math.PI;
    
            return value;
        }
    }
    
    private static class RingBuffer {
        private static final int INTERPOLATOR_MARGIN = 3;
        
        private final FloatBuffer buffer;
        private final int size;
        private int writeIndex;
        
        RingBuffer(int size) {
            this.buffer = FloatBuffer.allocate(size + INTERPOLATOR_MARGIN);
            this.size = size;
        }
        
        void writeMargined(float sample) {
            buffer.put(writeIndex, sample);
    
            if(writeIndex < INTERPOLATOR_MARGIN) {
                buffer.put(size + writeIndex, sample);
            }
    
            writeIndex++;
            if(writeIndex == size){
                writeIndex = 0;
            }
        }
    
        float getHermiteAt(float delay) {
            float fReadIndex = writeIndex - 1 - delay;
            while(fReadIndex < 0)
                fReadIndex += buffer.capacity();
            while(fReadIndex >= buffer.capacity())
                fReadIndex -= buffer.capacity();
        
            int iPart = (int)fReadIndex; // integer part of the delay
            float fPart = fReadIndex - iPart; // fractional part of the delay
    
            // Hermite polynomial interpolation
            // 4-point, 3rd-order Hermite (x-form)
            float c0 = buffer.get(iPart + 1);
            float c1 = 0.5f * (buffer.get(iPart + 2) - buffer.get(iPart));
            float c2 = (buffer.get(iPart) - 2.5f * buffer.get(iPart + 1)) + (2.0f*buffer.get(iPart + 2) - 0.5f * buffer.get(iPart + 3));
            float c3 = 0.5f * (buffer.get(iPart + 3) - buffer.get(iPart)) + 1.5f * (buffer.get(iPart + 1) - buffer.get(iPart + 2));
            return ((c3 * fPart + c2) * fPart + c1) * fPart + c0;
        }
    }
}
