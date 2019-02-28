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

package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.lava.common.natives.NativeResourceHolder;

public class KaraokeConverter extends NativeResourceHolder {
    private final long instance;

    public KaraokeConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        KaraokeNativeLibLoader.loadKaraokeLibrary();
        this.instance = KaraokeLibrary.create(sampleRate);
    }

    public void setLevel(float level) {
        checkNotReleased();
        KaraokeLibrary.setLevel(instance, level);
    }

    public void setMonoLevel(float level) {
        checkNotReleased();
        KaraokeLibrary.setMonoLevel(instance, level);
    }

    public void setFilterBand(float band) {
        checkNotReleased();
        KaraokeLibrary.setFilterBand(instance, band);
    }

    public void setFilterWidth(float width) {
        checkNotReleased();
        KaraokeLibrary.setFilterWidth(instance, width);
    }

    public void process(float[] inputLeft, float[] inputRight, int inputOffset, float[] outputLeft, float[] outputRight, int outputOffset, int samples) {
        checkNotReleased();
        KaraokeLibrary.process(instance, inputLeft, inputRight, inputOffset, outputLeft, outputRight, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        KaraokeLibrary.destroy(instance);
    }
}
