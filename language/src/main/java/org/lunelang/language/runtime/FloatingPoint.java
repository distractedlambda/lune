package org.lunelang.language.runtime;

public final class FloatingPoint {
    private FloatingPoint() {}

    public static boolean hasExactLongValue(double value) {
        return Double.isFinite(value) && value >= -0x1p63 && value < 0x1p63 && ((double) (long) value) == value;
    }

    public static boolean isNaN(double value) {
        return Double.isNaN(value);
    }
}
