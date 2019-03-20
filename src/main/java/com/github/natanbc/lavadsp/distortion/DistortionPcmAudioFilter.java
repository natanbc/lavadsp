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

package com.github.natanbc.lavadsp.distortion;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Applies simple math to each sample. Base formula is
 * <pre>
 * {@code
 * sampleSin = sinOffset + sin(sample * sinScale);
 * sampleCos = cosOffset + cos(sample * cosScale);
 * sampleTan = tanOffset + tan(sample * tanScale);
 * sample = max(-1, min(1, offset + scale * (useSin ? sampleSin : 1) * (useCos ? sampleCos : 1) * (useTan ? sampleTan : 1)));
 * }
 * </pre>
 */
public class DistortionPcmAudioFilter extends ConverterPcmAudioFilter<DistortionConverter> {
    public static final int SIN = DistortionConverter.SIN;
    public static final int COS = DistortionConverter.COS;
    public static final int TAN = DistortionConverter.TAN;
    
    private volatile float sinOffset = 0;
    private volatile float sinScale = 1;
    private volatile float cosOffset = 0;
    private volatile float cosScale = 1;
    private volatile float tanOffset = 0;
    private volatile float tanScale = 1;
    private volatile float offset = 0;
    private volatile float scale = 1;
    private final AtomicInteger enabled;

    public DistortionPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount) {
        super(new DistortionConverter(), downstream, channelCount);
        this.enabled = new AtomicInteger(DistortionConverter.ALL_FUNCTIONS);
    }

    /**
     * Enables the provided functions.
     *
     * <br>Example: {@code enableFunctions(DistortionPcmAudioFilter.SIN | DistortionPcmAudioFilter.COS)}
     *
     * @param functions Functions to enable. Multiple can be enabled by using a bitwise or.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter enableFunctions(int functions) {
        getConverter().enable(functions);
        enabled.updateAndGet(v->v | (functions & DistortionConverter.ALL_FUNCTIONS));
        return this;
    }

    /**
     * Disables the provided functions.
     *
     * <br>Example: {@code disableFunctions(DistortionPcmAudioFilter.SIN | DistortionPcmAudioFilter.COS)}
     *
     * @param functions Functions to disable. Multiple can be disabled by using a bitwise or.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter disableFunctions(int functions) {
        getConverter().disable(functions);
        enabled.updateAndGet(v->v & ~functions);
        return this;
    }

    /**
     * Returns whether or not a provided function is enabled.
     *
     * <br>If more than one function is provided, this method will return true if <b>any</b> of them
     * is enabled.
     *
     * @param function Function to check.
     *
     * @return {@code true} if the function is enabled.
     */
    public boolean isEnabled(int function) {
        return (enabled.get() & (function & DistortionConverter.ALL_FUNCTIONS)) != 0;
    }

    /**
     * Returns whether or not all provided functions are enabled.
     *
     * @param functions Functions to test for.
     *
     * @return {@code true} if all functions are enabled.
     */
    public boolean allEnabled(int functions) {
        return (enabled.get() & (functions & DistortionConverter.ALL_FUNCTIONS)) == functions;
    }

    /**
     * Returns the sin offset.
     *
     * @return The sin offset.
     */
    public float getSinOffset() {
        return sinOffset;
    }

    /**
     * Sets the sin offset.
     *
     * @param sinOffset New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setSinOffset(float sinOffset) {
        getConverter().setSinOffset(sinOffset);
        this.sinOffset = sinOffset;
        return this;
    }

    /**
     * Returns the sin scale.
     *
     * @return The sin scale.
     */
    public float getSinScale() {
        return sinScale;
    }

    /**
     * Sets the sin scale.
     *
     * @param sinScale New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setSinScale(float sinScale) {
        getConverter().setSinScale(sinScale);
        this.sinScale = sinScale;
        return this;
    }

    /**
     * Returns the cos offset.
     *
     * @return The cos offset.
     */
    public float getCosOffset() {
        return cosOffset;
    }

    /**
     * Sets the cos offset.
     *
     * @param cosOffset New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setCosOffset(float cosOffset) {
        getConverter().setCosOffset(cosOffset);
        this.cosOffset = cosOffset;
        return this;
    }

    /**
     * Returns the cos scale.
     *
     * @return The cos scale.
     */
    public float getCosScale() {
        return cosScale;
    }

    /**
     * Sets the cos scale.
     *
     * @param cosScale New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setCosScale(float cosScale) {
        getConverter().setCosScale(cosScale);
        this.cosScale = cosScale;
        return this;
    }

    /**
     * Returns the tan offset.
     *
     * @return The tan offset.
     */
    public float getTanOffset() {
        return tanOffset;
    }

    /**
     * Sets the tan offset.
     *
     * @param tanOffset New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setTanOffset(float tanOffset) {
        getConverter().setTanOffset(tanOffset);
        this.tanOffset = tanOffset;
        return this;
    }

    /**
     * Returns the tan scale.
     *
     * @return The tan scale.
     */
    public float getTanScale() {
        return tanScale;
    }

    /**
     * Sets the tan scale.
     *
     * @param tanScale New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setTanScale(float tanScale) {
        getConverter().setTanScale(tanScale);
        this.tanScale = tanScale;
        return this;
    }

    /**
     * Returns the offset.
     *
     * @return The offset.
     */
    public float getOffset() {
        return offset;
    }

    /**
     * Sets the offset.
     *
     * @param offset New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setOffset(float offset) {
        getConverter().setOffset(offset);
        this.offset = offset;
        return this;
    }

    /**
     * Returns the scale.
     *
     * @return The scale.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale New value to set.
     *
     * @return {@code this} for chaining calls.
     */
    public DistortionPcmAudioFilter setScale(float scale) {
        getConverter().setScale(scale);
        this.scale = scale;
        return this;
    }
}
