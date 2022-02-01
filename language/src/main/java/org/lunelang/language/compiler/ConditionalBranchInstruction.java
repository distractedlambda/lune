package org.lunelang.language.compiler;

public final class ConditionalBranchInstruction extends Instruction {
    private Instruction condition;
    private Block trueTarget, falseTarget;

    public ConditionalBranchInstruction(Instruction condition, Block trueTarget, Block falseTarget) {
        setCondition(condition);
        setTrueTarget(trueTarget);
        setFalseTarget(falseTarget);
    }

    public Instruction getCondition() {
        return condition;
    }

    public void setCondition(Instruction condition) {
        this.condition = condition;
    }

    public Block getTrueTarget() {
        return trueTarget;
    }

    public void setTrueTarget(Block trueTarget) {
        if (this.trueTarget != null && this.trueTarget != this.falseTarget) {
            this.trueTarget.removePredecessor(getBlock());
        }

        if (trueTarget != null) {
            trueTarget.addPredecessor(getBlock());
        }

        this.trueTarget = trueTarget;
    }

    public Block getFalseTarget() {
        return falseTarget;
    }

    public void setFalseTarget(Block falseTarget) {
        if (this.falseTarget != null && this.falseTarget != this.trueTarget) {
            this.falseTarget.removePredecessor(getBlock());
        }

        if (falseTarget != null) {
            falseTarget.addPredecessor(getBlock());
        }

        this.falseTarget = falseTarget;
    }
}
