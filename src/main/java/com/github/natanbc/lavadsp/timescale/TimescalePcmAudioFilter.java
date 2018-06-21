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

package com.github.natanbc.lavadsp.timescale;

import com.github.natanbc.lavadsp.natives.TimescaleConverter;
import com.github.natanbc.lavadsp.util.DoubleToDoubleFunction;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * <a href="https://en.wikipedia.org/wiki/Audio_time_stretching_and_pitch_scaling">Time stretch and pitch scale</a> filter implementation.
 */
public class TimescalePcmAudioFilter implements FloatPcmAudioFilter {
    private static final int BUFFER_SIZE = 4096;

    private final FloatPcmAudioFilter downstream;
    private final TimescaleConverter[] converters;
    private final float[][] outputSegments;
    private volatile double speed;
    private volatile double pitch;
    private volatile double rate;

    /**
     * @param downstream Next filter in chain.
     * @param channels Number of channels in input data.
     * @param sampleRate Sample rate.
     */
    public TimescalePcmAudioFilter(FloatPcmAudioFilter downstream, int channels, int sampleRate) {
        this.downstream = downstream;
        this.converters = new TimescaleConverter[channels];
        this.outputSegments = new float[channels][];
        this.speed = 1.0;
        this.pitch = 1.0;
        this.rate = 1.0;

        for (int i = 0; i < channels; i++) {
            outputSegments[i] = new float[BUFFER_SIZE];
            converters[i] = new TimescaleConverter(1, sampleRate);
        }
    }

    /**
     * Returns the current playback speed.
     *
     * @return The current playback speed. 1.0 means unmodified.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the playback speed. This calls the SoundTouch setTempo function.
     *
     * @param speed Speed to play at. 1.0 means unchanged.
     *
     * @return {@code this}, for chaining calls.
     */
    public TimescalePcmAudioFilter setSpeed(double speed) {
        for(TimescaleConverter converter : converters) {
            converter.setSpeed(speed);
        }
        this.speed = speed;
        return this;
    }

    /**
     * Updates the effect speed, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the speed.
     *
     * @return {@code this}, for chaining calls
     */
    public TimescalePcmAudioFilter updateFrequency(DoubleToDoubleFunction function) {
        return setSpeed(function.apply(speed));
    }

    /**
     * Sets the playback speed, in percentage, relative to the default.
     * <br>This is equivalent to {@code setSpeed(1.0 + 0.01 * change)}
     *
     * @param change Percentage relative to the default speed to play at.
     *
     * @return {@code this}, for chaining calls.
     */
    public TimescalePcmAudioFilter setSpeedChange(double change) {
        return setSpeed(1.0 + 0.01 * change);
    }

    /**
     * Returns the current pitch.
     *
     * @return The current pitch. 1.0 means unmodified.
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Sets the audio pitch.
     *
     * @param pitch Pitch to set. 1.0 means unchanged.
     *
     * @return {@code this}, for chaining calls.
     */
    public TimescalePcmAudioFilter setPitch(double pitch) {
        for(TimescaleConverter converter : converters) {
            converter.setPitch(pitch);
        }
        this.pitch = pitch;
        return this;
    }

    /**
     * Updates the effect pitch, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the pitch.
     *
     * @return {@code this}, for chaining calls
     */
    public TimescalePcmAudioFilter updatePitch(DoubleToDoubleFunction function) {
        return setPitch(function.apply(pitch));
    }

    /**
     * Sets the audio pitch in octaves. This is equivalent to {@code setPitch(Math.exp(0.69314718056 * Math.max(Math.min(pitch, 1.0), -1.0)))}.
     *
     * @param pitch Octaves to set.
     *
     * @return {@code this}, for chaining calls.
     *
     * @see #setPitch(double)
     */
    public TimescalePcmAudioFilter setPitchOctaves(double pitch) {
        return setPitch(Math.exp(0.69314718056 * Math.max(Math.min(pitch, 1.0), -1.0)));
    }

    /**
     * Sets the audio pitch in semi tones. This is equivalent to {@code setPitchOctaves(pitch / 12.0)}.
     *
     * @param pitch Semi tones to set.
     *
     * @return {@code this}, for chaining calls.
     *
     * @see #setPitch(double)
     * @see #setPitchOctaves(double)
     */
    public TimescalePcmAudioFilter setPitchSemiTones(double pitch) {
        return setPitchOctaves(pitch / 12.0);
    }

    /**
     * Returns the current playback rate.
     *
     * @return The current playback rate. 1.0 means unmodified.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Sets the audio rate, in percentage, relative to the default.
     *
     * @param rate Rate to set. 1.0 means unchanged.
     *
     * @return {@code this}, for chaining calls.
     */
    public TimescalePcmAudioFilter setRate(double rate) {
        for(TimescaleConverter converter : converters) {
            converter.setRate(rate);
        }
        this.rate = rate;
        return this;
    }

    /**
     * Updates the effect rate, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the rate.
     *
     * @return {@code this}, for chaining calls
     */
    public TimescalePcmAudioFilter updateRate(DoubleToDoubleFunction function) {
        return setRate(function.apply(rate));
    }

    /**
     * Sets the audio rate.
     * <br>This is equivalent to {@code setRate(1.0 + 0.01 * change)}
     *
     * @param change Percentage relative to the default rate to play at.
     *
     * @return {@code this}, for chaining calls.
     *
     * @see #setRate(double)
     */
    public TimescalePcmAudioFilter setRateChange(double change) {
        return setRate(1.0 + 0.01 * change);
    }

    /**
     * Queries processing sequence size in samples.
     *
     * <br>This value gives an approximate value of how many input samples you'll need to
     * feed into SoundTouch after initial buffering to get out a new batch of
     * output samples.
     * <br>
     * <br>This value does not include initial buffering at beginning of a new processing
     * stream, use {@link #getInitialLatencies()} to get the initial buffering size.
     *
     * @return Processing sequence size in samples.
     *
     * @see #getInitialLatencies()
     */
    public int[] getNominalInputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalInputSequence();
        }
        return ret;
    }

    /**
     * Queries nominal average processing output size in samples.
     *
     * <br>This value tells an approximate value of how many output samples
     * SoundTouch outputs once it does DSP processing run for a batch of input samples.
     *
     * @return Nominal average processing output size in samples.
     */
    public int[] getNominalOutputSequences() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getNominalOutputSequence();
        }
        return ret;
    }

    /**
     * Queries initial processing latencies, i.e.
     * approx. how many samples you'll need to enter to SoundTouch pipeline before
     * you can expect to get first batch of ready output samples out.
     *
     * @return Initial processing latencies.
     */
    public int[] getInitialLatencies() {
        int[] ret = new int[converters.length];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = converters[i].getInitialLatency();
        }
        return ret;
    }

    /**
     * Gets a setting from the converters.
     *
     * @param setting Setting to read.
     * @param <T> Type of the setting.
     *
     * @return The setting value.
     */
    public <T> T getSetting(Setting<T> setting) {
        //all converters always have the same settings
        return setting.get(converters[0]);
    }

    /**
     * Sets a converter setting.
     *
     * @param setting Setting to set.
     * @param value Value to set.
     * @param <T> Type of the setting.
     *
     * @return {@code true} if the setting was updated.
     */
    public <T> boolean setSetting(Setting<T> setting, T value) {
        boolean success = false;
        for(TimescaleConverter converter : converters) {
            success = setting.set(converter, value);
        }
        return success;
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        for (TimescaleConverter converter : converters) {
            converter.reset();
        }
    }

    @Override
    public void flush() {
        for(TimescaleConverter converter : converters) {
            converter.flush();
        }
    }

    @Override
    public void close() {
        for (TimescaleConverter converter : converters) {
            converter.close();
        }
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        int written = 0;
        for (int i = 0; i < input.length; i++) {
            written = converters[i].process(input[i], offset, length, outputSegments[i], 0, BUFFER_SIZE);
        }

        if(written > 0) {
            downstream.process(outputSegments, 0, written);
        }

        while(true) {
            boolean allEmpty = true;
            for(int i = 0; i < input.length; i++) {
                written = converters[i].read(outputSegments[i], 0, BUFFER_SIZE);
                if(written != 0) allEmpty = false;
            }
            if(allEmpty) break;
            downstream.process(outputSegments, 0, written);
        }
    }
}
