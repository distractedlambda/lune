package org.lunelang.language.compiler;

public abstract class Instruction {
    private int sourceOffset = -1;
    private int sourceLength = -1;

    private Instruction next, prior;
    private Block block;

    public final Instruction getNext() {
        return next;
    }

    public final void setNext(Instruction next) {
        this.next = next;
    }

    public final Instruction getPrior() {
        return prior;
    }

    public final void setPrior(Instruction prior) {
        this.prior = prior;
    }

    public final Block getBlock() {
        return block;
    }

    public final void setBlock(Block block) {
        this.block = block;
    }

    public final int getSourceOffset() {
        return sourceOffset;
    }

    public final void setSourceOffset(int sourceOffset) {
        this.sourceOffset = sourceOffset;
    }

    public final int getSourceLength() {
        return sourceLength;
    }

    public final void setSourceLength(int sourceLength) {
        this.sourceLength = sourceLength;
    }
}
