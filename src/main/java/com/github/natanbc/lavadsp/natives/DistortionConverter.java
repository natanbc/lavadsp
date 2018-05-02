package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class DistortionConverter extends NativeResourceHolder {
    public static final int SIN = DistortionLibrary.FUNCTION_SIN;
    public static final int COS = DistortionLibrary.FUNCTION_COS;
    public static final int TAN = DistortionLibrary.FUNCTION_TAN;

    private final long instance;
    private volatile double sinScale = 1;
    private volatile double cosScale = 1;
    private volatile double tanScale = 1;
    private volatile double scale = 1;
    private volatile double offset = 0;
    private volatile int enabled = SIN | COS | TAN;

    public DistortionConverter() {
        DistortionNativeLibLoader.loadDistortionLibrary();
        this.instance = DistortionLibrary.create();
    }

    public void enableFunctions(int functions) {
        checkNotReleased();
        enabled |= functions;
        DistortionLibrary.enable(instance, functions);
    }

    public void disableFunctions(int functions) {
        checkNotReleased();
        enabled &= ~functions;
        DistortionLibrary.disable(instance, functions);
    }

    public void setSinScale(double sinScale) {
        checkNotReleased();
        this.sinScale = sinScale;
        DistortionLibrary.setSinScale(instance, sinScale);
    }

    public void setCosScale(double cosScale) {
        checkNotReleased();
        this.cosScale = cosScale;
        DistortionLibrary.setCosScale(instance, cosScale);
    }

    public void setTanScale(double tanScale) {
        checkNotReleased();
        this.tanScale = tanScale;
        DistortionLibrary.setTanScale(instance, tanScale);
    }

    public void setScale(double scale) {
        checkNotReleased();
        this.scale = scale;
        DistortionLibrary.setScale(instance, scale);
    }

    public void setOffset(double offset) {
        checkNotReleased();
        this.offset = offset;
        DistortionLibrary.setOffset(instance, offset);
    }

    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        checkNotReleased();

        DistortionLibrary.process(instance, input, inputOffset, output, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        DistortionLibrary.destroy(instance);
    }
}
