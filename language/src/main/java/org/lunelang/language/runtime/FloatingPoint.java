package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import static java.lang.Double.doubleToRawLongBits;

public final class FloatingPoint {
    private FloatingPoint() {}

    public static boolean hasExactLongValue(double value) {
        return Double.isFinite(value) && value >= -0x1p63 && value < 0x1p63 && ((double) (long) value) == value;
    }

    public static boolean bitwiseEqual(double x, double y) {
        return doubleToRawLongBits(x) == doubleToRawLongBits(y);
    }

    public static boolean isNaN(double value) {
        return Double.isNaN(value);
    }

    @TruffleBoundary(allowInlining = true)
    public static double pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    @TruffleBoundary(allowInlining = true)
    public static double floorDivide(double dividend, double divisor) {
        return Math.floor(dividend / divisor);
    }

    @TruffleBoundary(allowInlining = true)
    public static double floorRemainder(double dividend, double divisor) {
        return dividend - divisor * floorDivide(dividend, divisor);
    }
}
