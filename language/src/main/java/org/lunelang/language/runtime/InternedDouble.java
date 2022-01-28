package org.lunelang.language.runtime;

public final class InternedDouble {
    private final double value;

    public InternedDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
