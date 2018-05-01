package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class TremoloNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;

    public static void loadTremoloLibrary() {
        if(loaded) return;
        NativeLibLoader.load(TremoloNativeLibLoader.class, "tremolo");
        criticalNativesAvailable = TremoloLibrary.criticalMethodsAvailable();
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean areCriticalNativesAvailable() {
        loadTremoloLibrary();
        return criticalNativesAvailable;
    }
}
