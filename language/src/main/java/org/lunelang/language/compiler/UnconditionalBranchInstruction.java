package org.lunelang.language.compiler;

public final class UnconditionalBranchInstruction extends Instruction {
    private Block target;

    public Block getTarget() {
        return target;
    }

    public void setTarget(Block target) {
        this.target = target;
    }
}
