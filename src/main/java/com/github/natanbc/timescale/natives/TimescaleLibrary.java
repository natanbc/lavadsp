package com.github.natanbc.timescale.natives;

class TimescaleLibrary {
    static TimescaleLibrary getInstance() {
        TimescaleNativeLibLoader.loadTimescaleLibrary();
        return new TimescaleLibrary();
    }

    native long create(int channels, int sampleRate, double speedRate);

    native void setSpeed(long instance, double rate);

    native void setPitch(long instance, double pitch);

    native int process(long instance, float[] input, int inputOffset, int inputLength, float[] output, int outputOffset, int outputLength, int[] written);

    native int read(long instance, float[] output, int outputOffset, int outputLength);

    native void reset(long instance);

    native void destroy(long instance);
}
