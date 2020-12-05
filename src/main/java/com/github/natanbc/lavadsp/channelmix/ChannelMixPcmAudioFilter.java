package com.github.natanbc.lavadsp.channelmix;

import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.github.natanbc.lavadsp.util.VectorSupport;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * This filter mixes both channels (left and right), with a configurable factor on how much
 * each channel affects the other. With the defaults, both channels are kept independent from
 * each other. Setting all factors to 0.5 means both channels get the same audio.
 */
public class ChannelMixPcmAudioFilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private float leftToLeft = 1f;
    private float leftToRight = 0f;
    private float rightToLeft = 0f;
    private float rightToRight = 1f;
    
    public ChannelMixPcmAudioFilter(FloatPcmAudioFilter downstream) {
        this.downstream = downstream;
    }
    
    /**
     * @return The current left-to-left factor. The default is 1.0.
     */
    public float getLeftToLeft() {
        return leftToLeft;
    }
    
    /**
     * @param leftToLeft The left-to-left factor. The default is 1.0.
     *
     * @return {@code this}, for chaining calls.
     */
    public ChannelMixPcmAudioFilter setLeftToLeft(float leftToLeft) {
        this.leftToLeft = leftToLeft;
        return this;
    }
    
    /**
     * Updates the left-to-left factor, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the factor.
     *
     * @return {@code this}, for chaining calls
     */
    public ChannelMixPcmAudioFilter updateLeftToLeft(FloatToFloatFunction function) {
        return setLeftToLeft(function.apply(leftToLeft));
    }
    
    /**
     * @return The current left-to-right factor. The default is 0.0.
     */
    public float getLeftToRight() {
        return leftToRight;
    }
    
    /**
     * @param leftToRight The left-to-right factor. The default is 0.0.
     *
     * @return {@code this}, for chaining calls.
     */
    public ChannelMixPcmAudioFilter setLeftToRight(float leftToRight) {
        this.leftToRight = leftToRight;
        return this;
    }
    
    /**
     * Updates the left-to-right factor, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the factor.
     *
     * @return {@code this}, for chaining calls
     */
    public ChannelMixPcmAudioFilter updateLeftToRight(FloatToFloatFunction function) {
        return setLeftToRight(function.apply(leftToRight));
    }
    
    /**
     * @return The current right-to-left factor. The default is 0.0.
     */
    public float getRightToLeft() {
        return rightToLeft;
    }
    
    /**
     * @param rightToLeft The right-to-left factor. The default is 0.0.
     *
     * @return {@code this}, for chaining calls.
     */
    public ChannelMixPcmAudioFilter setRightToLeft(float rightToLeft) {
        this.rightToLeft = rightToLeft;
        return this;
    }
    
    /**
     * Updates the right-to-left factor, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the factor.
     *
     * @return {@code this}, for chaining calls
     */
    public ChannelMixPcmAudioFilter updateRightToLeft(FloatToFloatFunction function) {
        return setRightToLeft(function.apply(rightToLeft));
    }
    
    /**
     * @return The current right-to-right factor. The default is 1.0.
     */
    public float getRightToRight() {
        return rightToRight;
    }
    
    /**
     * @param rightToRight The right-to-right factor. The default is 1.0.
     *
     * @return {@code this}, for chaining calls.
     */
    public ChannelMixPcmAudioFilter setRightToRight(float rightToRight) {
        this.rightToRight = rightToRight;
        return this;
    }
    
    /**
     * Updates the right-to-right factor, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the factor.
     *
     * @return {@code this}, for chaining calls
     */
    public ChannelMixPcmAudioFilter updateRightToRight(FloatToFloatFunction function) {
        return setRightToRight(function.apply(rightToRight));
    }
    
    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        if(input.length != 2) {
            downstream.process(input, offset, length);
            return;
        }
        float[] left = input[0];
        float[] right = input[1];
        VectorSupport.channelMix(left, right, offset, length,
                leftToLeft, leftToRight, rightToLeft, rightToRight);
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
