package org.lunelang.language.compiler;

public final class ConditionalBranchInstruction extends Instruction {
    private Instruction condition;
    private Block target;

    public Instruction getCondition() {
        return condition;
    }

    public void setCondition(Instruction condition) {
        this.condition = condition;
    }

    public Block getTarget() {
        return target;
    }

    public void setTarget(Block target) {
        this.target = target;
    }
}
