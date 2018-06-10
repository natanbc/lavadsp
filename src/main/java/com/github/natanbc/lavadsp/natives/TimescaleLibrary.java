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

class TimescaleLibrary {
    static native String soundTouchVersion();

    static native int soundTouchVersionID();

    static native boolean criticalMethodsAvailable();

    static native long create(int channels, int sampleRate);

    static native void setSpeed(long instance, double speed);

    static native void setPitch(long instance, double pitch);

    static native void setRate(long instance, double rate);

    static native int getSetting(long instance, int setting);

    static native boolean setSetting(long instance, int setting, int value);

    static native int process(long instance, float[] input, int inputOffset, int inputLength, float[] output, int outputOffset, int outputLength, int[] written);

    static native int read(long instance, float[] output, int outputOffset, int outputLength);

    static native void reset(long instance);

    static native double getInputOutputSampleRatio(long instance);

    static native void flush(long instance);

    static native int numUnprocessedSamples(long instance);

    static native int numSamples(long instance);

    static native boolean isEmpty(long instance);

    static native void destroy(long instance);
}
