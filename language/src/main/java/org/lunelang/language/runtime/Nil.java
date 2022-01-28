package org.lunelang.language.runtime;

import static java.lang.System.identityHashCode;

public final class Nil {
    private Nil() {}

    private static final Nil INSTANCE = new Nil();

    private static final int HASH_CODE = identityHashCode(INSTANCE);

    public static Nil getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean equals(Object object) {
        return object == INSTANCE;
    }

    @Override
    public int hashCode() {
        return HASH_CODE;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
