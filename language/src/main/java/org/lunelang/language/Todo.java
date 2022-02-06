package org.lunelang.language;

public final class Todo {
    private Todo() {}

    public static RuntimeException TODO() {
        throw new UnsupportedOperationException();
    }
}
