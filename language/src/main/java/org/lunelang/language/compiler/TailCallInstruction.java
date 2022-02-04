package org.lunelang.language.compiler;

public final class TailCallInstruction extends VariadicInstruction {
    private Instruction callee;

    public TailCallInstruction(Instruction callee) {
        this.callee = callee;
    }

    public Instruction getCallee() {
        return callee;
    }

    public void setCallee(Instruction callee) {
        this.callee = callee;
    }
}
