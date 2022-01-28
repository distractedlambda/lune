package org.lunelang.language.runtime;

public final class InternedBoolean {
    private final boolean value;

    public InternedBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
