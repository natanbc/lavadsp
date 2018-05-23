package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeResourceHolder;

public class DistortionConverter extends NativeResourceHolder implements Converter {
    public static final int SIN = DistortionLibrary.FUNCTION_SIN;
    public static final int COS = DistortionLibrary.FUNCTION_COS;
    public static final int TAN = DistortionLibrary.FUNCTION_TAN;

    private final long instance;

    public DistortionConverter() {
        DistortionNativeLibLoader.loadDistortionLibrary();
        this.instance = DistortionLibrary.create();
    }

    public void enableFunctions(int functions) {
        checkNotReleased();
        DistortionLibrary.enable(instance, functions);
    }

    public void disableFunctions(int functions) {
        checkNotReleased();
        DistortionLibrary.disable(instance, functions);
    }

    public void setSinOffset(double sinOffset) {
        checkNotReleased();
        DistortionLibrary.setSinOffset(instance, sinOffset);
    }

    public void setSinScale(double sinScale) {
        checkNotReleased();
        DistortionLibrary.setSinScale(instance, sinScale);
    }

    public void setCosOffset(double cosOffset) {
        checkNotReleased();
        DistortionLibrary.setCosOffset(instance, cosOffset);
    }

    public void setCosScale(double cosScale) {
        checkNotReleased();
        DistortionLibrary.setCosScale(instance, cosScale);
    }

    public void setTanOffset(double tanOffset) {
        checkNotReleased();
        DistortionLibrary.setTanOffset(instance, tanOffset);
    }

    public void setTanScale(double tanScale) {
        checkNotReleased();
        DistortionLibrary.setTanScale(instance, tanScale);
    }

    public void setOffset(double offset) {
        checkNotReleased();
        DistortionLibrary.setOffset(instance, offset);
    }

    public void setScale(double scale) {
        checkNotReleased();
        DistortionLibrary.setScale(instance, scale);
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
