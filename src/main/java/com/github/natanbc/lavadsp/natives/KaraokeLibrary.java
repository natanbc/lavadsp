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

class KaraokeLibrary {
    static native boolean criticalMethodsAvailable();

    static native long create(int sampleRate);

    static native void setLevel(long instance, float level);

    static native void setMonoLevel(long instance, float level);

    static native void setFilterBand(long instance, float band);

    static native void setFilterWidth(long instance, float width);

    static native void process(long instance, float[] inputLeft, float[] inputRight, int inputOffset, float[] outputLeft, float[] outputRight, int outputOffset, int samples);

    static native void destroy(long instance);
}
