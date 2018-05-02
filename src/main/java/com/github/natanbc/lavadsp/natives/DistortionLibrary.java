package com.github.natanbc.lavadsp.natives;

class DistortionLibrary {
    static final int FUNCTION_SIN = 1;
    static final int FUNCTION_COS = 2;
    static final int FUNCTION_TAN = 4;

    static native boolean criticalMethodsAvailable();

    static native long create();

    static native void setSinScale(long instance, double scale);

    static native void setCosScale(long instance, double scale);

    static native void setTanScale(long instance, double scale);

    static native void setScale(long instance, double scale);

    static native void setOffset(long instance, double offset);

    static native void enable(long instance, int functions);

    static native void disable(long instance, int functions);

    static native void process(long instance, float[] input, int inputOffset, float[] output, int outputOffset, int samples);

    static native void destroy(long instance);
}
