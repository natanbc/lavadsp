package com.github.natanbc.lavadsp.natives;

class TremoloLibrary {
    static native boolean criticalMethodsAvailable();

    static native long create(int sampleRate);

    static native void setFrequency(long instance, double frequency);

    static native void setDepth(long instance, double depth);

    static native void process(long instance, float[] input, int inputOffset, float[] output, int outputOffset, int samples);

    static native void destroy(long instance);
}
