package com.github.natanbc.timescale.natives;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class TimescaleNativeLibLoader {
    public static void loadTimescaleLibrary() {
        NativeLibLoader.load(TimescaleNativeLibLoader.class, "timescale");
    }
}
