package com.github.natanbc.timescale.natives;

class TimescaleLibrary {
    static native String soundTouchVersion();

    static native int soundTouchVersionID();

    static native boolean criticalMethodsAvailable();

    static native long create(int channels, int sampleRate, double speedRate);

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
