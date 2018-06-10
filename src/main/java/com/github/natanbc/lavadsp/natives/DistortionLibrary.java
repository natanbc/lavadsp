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

class DistortionLibrary {
    static final int FUNCTION_SIN = 1;
    static final int FUNCTION_COS = 2;
    static final int FUNCTION_TAN = 4;

    static native boolean criticalMethodsAvailable();

    static native int allFunctions();

    static native long create();

    static native void setSinOffset(long instance, double offset);

    static native void setSinScale(long instance, double scale);

    static native void setCosOffset(long instance, double offset);

    static native void setCosScale(long instance, double scale);

    static native void setTanOffset(long instance, double offset);

    static native void setTanScale(long instance, double scale);

    static native void setOffset(long instance, double offset);

    static native void setScale(long instance, double scale);

    static native void enable(long instance, int functions);

    static native void disable(long instance, int functions);

    static native void process(long instance, float[] input, int inputOffset, float[] output, int outputOffset, int samples);

    static native void destroy(long instance);
}
