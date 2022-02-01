package org.lunelang.language.compiler;

public abstract class Instruction {
    private Instruction next, prior;
    private Block block;

    public final Instruction getNext() {
        return next;
    }

    public void setNext(Instruction next) {
        this.next = next;
    }

    public final Instruction getPrior() {
        return prior;
    }

    public void setPrior(Instruction prior) {
        this.prior = prior;
    }

    public final Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
