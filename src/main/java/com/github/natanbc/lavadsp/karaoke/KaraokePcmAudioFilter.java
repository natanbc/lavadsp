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

package com.github.natanbc.lavadsp.karaoke;

import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

public class KaraokePcmAudioFilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private final KaraokeConverter converter;
    private volatile float level = 1;
    private volatile float monoLevel = 1;
    private volatile float filterBand = 220;
    private volatile float filterWidth = 100;

    public KaraokePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int sampleRate) {
        this.downstream = downstream;
        if(channelCount == 2) {
            this.converter = new KaraokeConverter(sampleRate);
        } else {
            this.converter = null;
        }
    }

    /**
     * Returns whether or not this converter is enabled. It will only be enabled if
     * the channel count given to the constructor is {@code 2}. If it's a different value,
     * this filter is no-op.
     *
     * @return Whether or not this filter is enabled.
     */
    public boolean isEnabled() {
        return converter != null;
    }

    /**
     * Returns the current level.
     *
     * @return The current level.
     */
    public float getLevel() {
        return level;
    }

    /**
     * Sets the effect level.
     *
     * @param level Level to set.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter setLevel(float level) {
        this.level = level;
        if(converter != null) {
            converter.setLevel(level);
        }
        return this;
    }

    /**
     * Updates the effect level, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the level.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter updateLevel(FloatToFloatFunction function) {
        return setLevel(function.apply(level));
    }

    /**
     * Returns the current mono level.
     *
     * @return The current mono level.
     */
    public float getMonoLevel() {
        return monoLevel;
    }

    /**
     * Sets the effect mono level.
     *
     * @param level Mono level to set.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter setMonoLevel(float level) {
        this.monoLevel = level;
        if(converter != null) {
            converter.setMonoLevel(level);
        }
        return this;
    }

    /**
     * Updates the effect mono level, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the mono level.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter updateMonoLevel(FloatToFloatFunction function) {
        return setMonoLevel(function.apply(monoLevel));
    }

    /**
     * Returns the current filter band.
     *
     * @return The current filter band.
     */
    public float getFilterBand() {
        return filterBand;
    }

    /**
     * Sets the effect filter band.
     *
     * @param band Filter band to set.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter setFilterBand(float band) {
        this.filterBand = band;
        if(converter != null) {
            converter.setFilterBand(band);
        }
        return this;
    }

    /**
     * Updates the effect filter band, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the filter band.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter updateFilterBand(FloatToFloatFunction function) {
        return setFilterBand(function.apply(filterBand));
    }

    /**
     * Returns the current filter width.
     *
     * @return The current filter width.
     */
    public float getFilterWidth() {
        return filterWidth;
    }

    /**
     * Sets the effect filter width.
     *
     * @param width Filter width to set.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter setFilterWidth(float width) {
        this.filterWidth = level;
        if(converter != null) {
            converter.setFilterWidth(width);
        }
        return this;
    }

    /**
     * Updates the effect filter width, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the filter width.
     *
     * @return {@code this}, for chaining calls
     */
    public KaraokePcmAudioFilter updateFilterWidth(FloatToFloatFunction function) {
        return setFilterWidth(function.apply(filterWidth));
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        if(converter == null || input.length != 2) {
            downstream.process(input, offset, length);
            return;
        }
        float[] left = input[0];
        float[] right = input[1];
        converter.process(left, right, offset, left, right, 0, length);
        downstream.process(input, 0, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        //nothing to do here
    }

    @Override
    public void flush() {
        //nothing to do here
    }

    @Override
    public void close() {
        //nothing to do here
    }
}
