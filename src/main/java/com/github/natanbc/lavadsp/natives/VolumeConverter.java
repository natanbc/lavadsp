package com.github.natanbc.lavadsp.natives;


import com.sedmelluq.lava.common.natives.NativeResourceHolder;

public class VolumeConverter extends NativeResourceHolder implements Converter {
    private final long instance;

    public VolumeConverter() {
        VolumeNativeLibLoader.loadVolumeLibrary();
        this.instance = VolumeLibrary.create();
    }

    public void setVolume(double volume) {
        checkNotReleased();

        if(volume <= 0) {
            throw new IllegalArgumentException("Volume <= 0.0");
        }
        if(volume > 5) {
            throw new IllegalArgumentException("Volume > 5.0");
        }

        VolumeLibrary.setVolume(instance, volume);
    }

    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        checkNotReleased();

        VolumeLibrary.process(instance, input, inputOffset, output, outputOffset, samples);
    }

    @Override
    protected void freeResources() {
        VolumeLibrary.destroy(instance);
    }
}
