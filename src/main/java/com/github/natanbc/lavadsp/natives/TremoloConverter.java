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

public class TremoloConverter extends NativeResourceHolder implements Converter {
    private final long instance;

    public TremoloConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        TremoloNativeLibLoader.loadTremoloLibrary();
        this.instance = TremoloLibrary.create(sampleRate);
    }

    public void setDepth(double depth) {
        checkNotReleased();

        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }

        TremoloLibrary.setDepth(instance, depth);
    }

    public void setFrequency(double frequency) {
        checkNotReleased();

        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        TremoloLibrary.setFrequency(instance, frequency);
    }

    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        checkNotReleased();

        TremoloLibrary.process(instance, input, inputOffset, output, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        TremoloLibrary.destroy(instance);
    }
}
