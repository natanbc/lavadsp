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
