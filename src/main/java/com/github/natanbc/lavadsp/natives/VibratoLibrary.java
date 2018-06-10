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

class VibratoLibrary {
    static native boolean criticalMethodsAvailable();

    static native float maxFrequency();

    static native long create(int sampleRate);

    static native void setFrequency(long instance, float frequency);

    static native void setDepth(long instance, float depth);

    static native void process(long instance, float[] input, int inputOffset, float[] output, int outputOffset, int samples);

    static native void destroy(long instance);
}
