package com.github.natanbc.lavadsp.rotation;

import com.github.natanbc.lavadsp.util.DoubleToDoubleFunction;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * This filter simulates an audio source rotating around the listener.
 */
public class RotationPcmAudioFilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private final int sampleRate;
    private double rotationHz;
    private double x;
    private double dI;
    
    public RotationPcmAudioFilter(FloatPcmAudioFilter downstream, int sampleRate) {
        this.downstream = downstream;
        this.sampleRate = sampleRate;
        setRotationSpeed(5d);
    }
    
    /**
     * @return The current rotation speed. The default is 5.0.
     */
    public double getRotationHz() {
        return rotationHz;
    }
    
    /**
     * @param rotationHz The frequency the audio should rotate around the listener, in Hertz.
     *
     * @return {@code this}, for chaining calls.
     */
    public RotationPcmAudioFilter setRotationSpeed(double rotationHz) {
        this.rotationHz = rotationHz;
        double samplesPerCycle = sampleRate / (rotationHz * 2 * Math.PI);
        this.dI = rotationHz == 0 ? 0 : 1 / samplesPerCycle;
        return this;
    }
    
    /**
     * Updates the rotation speed, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the speed.
     *
     * @return {@code this}, for chaining calls
     */
    public RotationPcmAudioFilter updateRotationSpeed(DoubleToDoubleFunction function) {
        return setRotationSpeed(function.apply(rotationHz));
    }
    
    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        if(input.length != 2) {
            downstream.process(input, offset, length);
            return;
        }
        float[] left = input[0];
        float[] right = input[1];
        for(int i = 0; i < length; i++) {
            //sin(x) and cos(x) return a value in the range [-1, 1], but we want [0, 1], so
            //add one to move to [0, 2] and divide by 2 to obtain [0, 1]
            double sin = Math.sin(x);
            left[offset + i] = left[offset + i] * (float)(sin + 1f) / 2f;
            //cos(x + pi/2) == -sin(x), so just reuse the already computed sine value
            right[offset + i] = right[offset + i] * (float)(-sin + 1f) / 2f;
            x += dI;
        }
        downstream.process(input, offset, length);
    }
    
    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        //nothing to do
    }
    
    @Override
    public void flush() {
        //nothing to do
    }
    
    @Override
    public void close() {
        //nothing to do
    }
}
