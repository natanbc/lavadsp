package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class VolumeNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;

    public static void loadVolumeLibrary() {
        if(loaded) return;
        NativeLibLoader.load(VolumeNativeLibLoader.class, "volume");
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
