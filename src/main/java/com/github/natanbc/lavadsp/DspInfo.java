package com.github.natanbc.lavadsp;

public final class DspInfo {
    public static final String VERSION_MAJOR;
    public static final String VERSION_MINOR;
    public static final String VERSION_REVISION;
    public static final String COMMIT_HASH;

    public static final String VERSION;

    static {
        VERSION_MAJOR = "@VERSION_MAJOR@";
        VERSION_MINOR = "@VERSION_MINOR@";
        VERSION_REVISION = "@VERSION_REVISION@";
        COMMIT_HASH = "@COMMIT_HASH@";
        //noinspection ConstantConditions
        VERSION = VERSION_MAJOR.startsWith("@") ? "Dev" : String.format("%s.%s.%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
    }

    private DspInfo() {}
}
