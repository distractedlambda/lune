package org.lunelang.language.runtime;

public final class InternedLong {
    private final long value;

    public InternedLong(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
