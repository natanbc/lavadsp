package com.github.natanbc.lavadsp.timescale;

import com.github.natanbc.lavadsp.natives.TimescaleConverter;

import java.util.Objects;

public abstract class Setting<T> {
    /**
     * Enable/disable anti-alias filter in pitch transposer.
     */
    public static final Setting<Boolean> USE_ANTI_ALIASING_FILTER = new Setting<Boolean>("USE_ANTI_ALIASING_FILTER", 0) {
        @Override
        public Boolean get(TimescaleConverter converter) {
            return converter.getSetting(id) == 1;
        }

        @Override
        public boolean set(TimescaleConverter converter, Boolean value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value ? 1 : 0);
        }
    };

    /**
     * Pitch transposer anti-alias filter length.
     */
    public static final Setting<Integer> ANTI_ALIASING_FILTER_LENGTH = new Setting<Integer>("ANTI_ALIASING_FILTER_LENGTH", 1) {
        @Override
        public Integer get(TimescaleConverter converter) {
            return converter.getSetting(id);
        }

        @Override
        public boolean set(TimescaleConverter converter, Integer value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value);
        }
    };

    /**
     * Enable/disable quick seeking algorithm in tempo changer routine
     * (enabling quick seeking lowers CPU utilization but causes a minor sound
     * quality compromising).
     */
    public static final Setting<Boolean> USE_QUICK_SEEK = new Setting<Boolean>("USE_QUICK_SEEK", 2) {
        @Override
        public Boolean get(TimescaleConverter converter) {
            return converter.getSetting(id) == 1;
        }

        @Override
        public boolean set(TimescaleConverter converter, Boolean value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value ? 1 : 0);
        }
    };

    /**
     * Time-stretch algorithm single processing sequence length in milliseconds. This determines
     * to how long sequences the original sound is chopped in the time-stretch algorithm.
     */
    public static final Setting<Integer> SEQUENCE_MS = new Setting<Integer>("SEQUENCE_MS", 3) {
        @Override
        public Integer get(TimescaleConverter converter) {
            return converter.getSetting(id);
        }

        @Override
        public boolean set(TimescaleConverter converter, Integer value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value);
        }
    };

    /**
     * Time-stretch algorithm seeking window length in milliseconds for algorithm that finds the
     * best possible overlapping location. This determines from how wide window the algorithm
     * may look for an optimal joining location when mixing the sound sequences back together.
     */
    public static final Setting<Integer> SEEKWINDOW_MS = new Setting<Integer>("SEEKWINDOW_MS", 4) {
        @Override
        public Integer get(TimescaleConverter converter) {
            return converter.getSetting(id);
        }

        @Override
        public boolean set(TimescaleConverter converter, Integer value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value);
        }
    };

    /**
     * Time-stretch algorithm overlap length in milliseconds. When the chopped sound sequences
     * are mixed back together, to form a continuous sound stream, this parameter defines over
     * how long period the two consecutive sequences are let to overlap each other.
     */
    public static final Setting<Integer> OVERLAP_MS = new Setting<Integer>("OVERLAP_MS", 5) {
        @Override
        public Integer get(TimescaleConverter converter) {
            return converter.getSetting(id);
        }

        @Override
        public boolean set(TimescaleConverter converter, Integer value) {
            Objects.requireNonNull(value);
            return converter.setSetting(id, value);
        }
    };

    private final String name;
    final int id;

    Setting(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public abstract T get(TimescaleConverter converter);

    public abstract boolean set(TimescaleConverter converter, T value);

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Setting(" + name + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Setting && ((Setting<?>)obj).id == id;
    }
}
