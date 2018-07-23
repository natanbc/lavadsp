package com.github.natanbc.lavadsp.natives;

public class VolumeLibrary {
    static native boolean criticalMethodsAvailable();

    static native long create();

    static native void setVolume(long instance, double volume);

    static native void process(long instance, float[] input, int inputOffset, float[] output, int outputOffset, int samples);

    static native void destroy(long instance);
}
