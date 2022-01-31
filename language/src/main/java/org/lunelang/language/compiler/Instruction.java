package org.lunelang.language.compiler;

public abstract class Instruction {
    private Instruction next, prior;
    private Block block;

    public final Instruction getNext() {
        return next;
    }

    public final Instruction getPrior() {
        return prior;
    }

    public final Block getBlock() {
        return block;
    }
}
