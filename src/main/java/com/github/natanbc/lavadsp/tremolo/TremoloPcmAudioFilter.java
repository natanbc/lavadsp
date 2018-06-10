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

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.TremoloConverter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * <a href="https://en.wikipedia.org/wiki/Tremolo">Tremolo</a> filter implementation.
 */
public class TremoloPcmAudioFilter extends ConverterPcmAudioFilter<TremoloConverter> {
    private volatile double frequency = 2f;
    private volatile double depth = 0.5f;

    public TremoloPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        super(new TremoloConverter(sampleRate), downstream, channelCount);
        setDepth(0.5);
        setFrequency(4);
    }

    /**
     * Returns the effect frequency.
     *
     * @return The effect frequency.
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Sets the effect frequency.
     *
     * @param frequency Frequency to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public TremoloPcmAudioFilter setFrequency(double frequency) {
        getConverter().setFrequency(frequency);
        this.frequency = frequency;
        return this;
    }

    /**
     * Returns the effect depth.
     *
     * @return The effect depth.
     */
    public double getDepth() {
        return depth;
    }

    /**
     * Sets the effect depth.
     *
     * @param depth Depth to set.
     *
     * @return {@code this}, for chaining calls.
     */
    public TremoloPcmAudioFilter setDepth(double depth) {
        getConverter().setDepth(depth);
        this.depth = depth;
        return this;
    }
}