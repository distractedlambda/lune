package org.lunelang.language.compiler;

public final class CallInstruction extends VariadicInstruction {
    private Instruction callee;

    public CallInstruction(Instruction callee) {
        this.callee = callee;
    }

    public Instruction getCallee() {
        return callee;
    }

    public void setCallee(Instruction callee) {
        this.callee = callee;
    }
}
