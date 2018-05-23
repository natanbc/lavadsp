package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class DistortionNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;
    private static volatile int allFunctions;

    public static void loadDistortionLibrary() {
        if(loaded) return;
        NativeLibLoader.load(DistortionNativeLibLoader.class, "distortion");
        criticalNativesAvailable = DistortionLibrary.criticalMethodsAvailable();
        allFunctions = DistortionLibrary.allFunctions();
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean areCriticalNativesAvailable() {
        loadDistortionLibrary();
        return criticalNativesAvailable;
    }

    public static int allFunctions() {
        loadDistortionLibrary();
        return allFunctions;
    }
}
