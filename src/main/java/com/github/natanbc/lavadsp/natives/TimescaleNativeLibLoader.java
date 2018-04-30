package com.github.natanbc.lavadsp.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class TimescaleNativeLibLoader {
    private static volatile boolean loaded = false;
    private static volatile String soundTouchVersion;
    private static volatile int soundTouchVersionID;
    private static volatile boolean criticalNativesAvailable;

    public static void loadTimescaleLibrary() {
        if(loaded) return;
        NativeLibLoader.load(TimescaleNativeLibLoader.class, "timescale");
        loaded = true;
        soundTouchVersion = TimescaleLibrary.soundTouchVersion();
        soundTouchVersionID = TimescaleLibrary.soundTouchVersionID();
        criticalNativesAvailable = TimescaleLibrary.criticalMethodsAvailable();
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static String getSoundTouchVersion() {
        loadTimescaleLibrary();
        return soundTouchVersion;
    }

    public static int getSoundTouchVersionID() {
        loadTimescaleLibrary();
        return soundTouchVersionID;
    }

    public static boolean areCriticalNativesAvailable() {
        loadTimescaleLibrary();
        return criticalNativesAvailable;
    }
}
