package org.lunelang.language.compiler;

public final class Function {
    private final Block entryBlock;

    public Function() {
        entryBlock = new Block(this);
    }

    public Block getEntryBlock() {
        return entryBlock;
    }
}
