package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.lava.common.natives.NativeLibraryLoader;

public class VolumeNativeLibLoader {
    private static final NativeLibraryLoader LOADER = NativeLibraryLoader.create(VolumeNativeLibLoader.class, "volume");
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;

    public static void loadVolumeLibrary() {
        if(loaded) return;
        LOADER.load();
        criticalNativesAvailable = VolumeLibrary.criticalMethodsAvailable();
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean areCriticalNativesAvailable() {
        loadVolumeLibrary();
        return criticalNativesAvailable;
    }
}
