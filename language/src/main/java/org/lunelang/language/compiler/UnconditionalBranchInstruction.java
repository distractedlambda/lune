package org.lunelang.language.compiler;

public final class UnconditionalBranchInstruction extends Instruction {
    private Block target;

    public UnconditionalBranchInstruction(Block target) {
        setTarget(target);
    }

    public Block getTarget() {
        return target;
    }

    public void setTarget(Block target) {
        if (this.target != null) {
            this.target.removePredecessor(getBlock());
        }

        if (target != null) {
            target.addPredecessor(getBlock());
        }

        this.target = target;
    }
}
