package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class DistortionNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;

    public static void loadDistortionLibrary() {
        if(loaded) return;
        NativeLibLoader.load(DistortionNativeLibLoader.class, "distortion");
        criticalNativesAvailable = DistortionLibrary.criticalMethodsAvailable();
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean areCriticalNativesAvailable() {
        loadDistortionLibrary();
        return criticalNativesAvailable;
    }
}
