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

package com.github.natanbc.lavadsp.vibrato;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * <a href="https://en.wikipedia.org/wiki/Vibrato">Vibrato</a> filter implementation.
 *
 * Ported from <a href="https://github.com/Bershov/Vibrato-effect">BerVibrato</a>
 */
public class VibratoPcmAudioFilter extends ConverterPcmAudioFilter<VibratoConverter> {
    //values taken from BerVibrato.h
    private volatile float frequency = 2f;
    private volatile float depth = 0.5f;

    public VibratoPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        super(new VibratoConverter(sampleRate), downstream, channelCount);
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
    public VibratoPcmAudioFilter setFrequency(float frequency) {
        getConverter().setFrequency(frequency);
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
    public VibratoPcmAudioFilter updateFrequency(FloatToFloatFunction function) {
        return setFrequency(function.apply(frequency));
    }

    /**
     * Returns the effect depth.
     *
     * @return The effect depth.
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the effect depth.
     *
     * @param depth Depth to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public VibratoPcmAudioFilter setDepth(float depth) {
        getConverter().setDepth(depth);
        this.depth = depth;
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
    public VibratoPcmAudioFilter updateDepth(FloatToFloatFunction function) {
        return setDepth(function.apply(depth));
    }
}
