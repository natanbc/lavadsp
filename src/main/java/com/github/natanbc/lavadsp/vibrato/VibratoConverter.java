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
            int maxDelay = (int)(BASE_DELAY_SEC * sampleRate);
    
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
            float dp = 2 * (float)Math.PI * frequency / sampleRate;
            float value = (float)((Math.sin(phase) + 1) * 0.5);
            phase += dp;
            while(phase > 2 * Math.PI) {
                phase -= 2 * Math.PI;
            }
            return value;
        }
    }
    
    private static class RingBuffer {
        private static final int INTERPOLATOR_MARGIN = 3;
        
        private final float[] buffer;
        private final int size;
        private int writeIndex;
        
        RingBuffer(int size) {
            this.buffer = new float[size + INTERPOLATOR_MARGIN];
            this.size = size;
        }
        
        void writeMargined(float sample) {
            buffer[writeIndex] = sample;
    
            if(writeIndex < INTERPOLATOR_MARGIN) {
                buffer[size + writeIndex] = sample;
            }
    
            writeIndex++;
            if(writeIndex == size){
                writeIndex = 0;
            }
        }
    
        float getHermiteAt(float delay) {
            float fReadIndex = writeIndex - 1 - delay;
            while(fReadIndex < 0) {
                fReadIndex += size;
            }
            while(fReadIndex >= size) {
                fReadIndex -= size;
            }
        
            int iPart = (int)fReadIndex; // integer part of the delay
            float fPart = fReadIndex - iPart; // fractional part of the delay
    
            return getSampleHermite4p3o(fPart, buffer, iPart);
        }
        
        // Hermite polynomial interpolation
        // 4-point, 3rd-order Hermite (x-form)
        private static float getSampleHermite4p3o(float x, float[] buffer, int offset) {
            float y0 = buffer[offset];
            float y1 = buffer[offset + 1];
            float y2 = buffer[offset + 2];
            float y3 = buffer[offset + 3];
        
            //c0 = y[1];
            //c1 = (1.0/2.0)*(y[2]-y[0]);
            float c1 = (1f / 2f) * (y2 - y0);
            //c2 = (y[0] - (5.0/2.0)*y[1]) + (2.0*y[2] - (1.0/2.0)*y[3]);
            float c2 = (y0 - (5f / 2f) * y1) + (2f * y2 - (1f / 2f) * y3);
            //c3 = (1.0/2.0)*(y[3]-y[0]) + (3.0/2.0)*(y[1]-y[2]);
            float c3 = (1f / 2f) * (y3 - y0) + (3f / 2f) * (y1 - y2);
            return ((c3 * x + c2) * x + c1) * x + y1;
        }
    }
}
