/*
 * Copyright 2018 natanbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.natanbc.lavadsp.tremolo;

import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.github.natanbc.lavadsp.util.VectorSupport;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * <a href="https://en.wikipedia.org/wiki/Tremolo">Tremolo</a> filter implementation.
 */
public class TremoloPcmAudioFilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private final int sampleRate;
    private final float[] phases;
    private float frequency = 2f;
    private float depth = 0.5f;

    public TremoloPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        this.downstream = downstream;
        this.sampleRate = sampleRate;
        this.phases = new float[channelCount];
    }

    /**
     * Returns the effect frequency.
     *
     * @return The effect frequency.
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * Sets the effect frequency.
     *
     * @param frequency Frequency to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public TremoloPcmAudioFilter setFrequency(float frequency) {
        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        this.frequency = frequency;
        return this;
    }

    /**
     * Updates the effect frequency, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the frequency.
     *
     * @return {@code this}, for chaining calls
     */
    public TremoloPcmAudioFilter updateFrequency(FloatToFloatFunction function) {
        return setFrequency(function.apply(frequency));
    }

    /**
     * Returns the effect depth.
     *
     * @return The effect depth.
     */
    public float getDepth() {
        return depth * 2;
    }

    /**
     * Sets the effect depth.
     *
     * @param depth Depth to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public TremoloPcmAudioFilter setDepth(float depth) {
        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }
        //we want (0, 0.5]
        this.depth = depth / 2;
        return this;
    }

    /**
     * Updates the effect depth, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the depth.
     *
     * @return {@code this}, for chaining calls
     */
    public TremoloPcmAudioFilter updateDepth(FloatToFloatFunction function) {
        return setDepth(function.apply(depth * 2));
    }
    
    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        for(int channel = 0; channel < input.length; channel++) {
            phases[channel] = VectorSupport.tremolo(
                    input[channel], offset, length,
                    sampleRate, frequency, depth, phases[channel]
            );
        }
        downstream.process(input, offset, length);
    }
    
    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        //nothing to do
    }
    
    @Override
    public void flush() throws InterruptedException {
        //nothing to do
    }
    
    @Override
    public void close() {
        //nothing to do
    }
}