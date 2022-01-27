package org.lunelang.language.runtime;

public final class Nil {
    private Nil() {}

    private static final Nil INSTANCE = new Nil();

    public static Nil getInstance() {
        return INSTANCE;
    }
}
