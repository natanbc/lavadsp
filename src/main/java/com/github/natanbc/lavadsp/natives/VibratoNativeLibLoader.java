package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class VibratoNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;
    private static volatile float maxFrequency;

    public static void loadVibratoLibrary() {
        if(loaded) return;
        NativeLibLoader.load(VibratoNativeLibLoader.class, "vibrato");
        loaded = true;
        criticalNativesAvailable = VibratoLibrary.criticalMethodsAvailable();
        maxFrequency = VibratoLibrary.maxFrequency();
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean areCriticalNativesAvailable() {
        loadVibratoLibrary();
        return criticalNativesAvailable;
    }

    public static float maxFrequency() {
        loadVibratoLibrary();;
        return maxFrequency;
    }
}
