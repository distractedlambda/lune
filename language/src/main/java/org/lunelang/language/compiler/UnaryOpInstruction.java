package org.lunelang.language.compiler;

public final class UnaryOpInstruction extends Instruction {
    UnaryOp op;
    Instruction operand;

    public Instruction getOperand() {
        return operand;
    }

    public void setOperand(Instruction operand) {
        this.operand = operand;
    }

    public UnaryOp getOp() {
        return op;
    }

    public void setOp(UnaryOp op) {
        this.op = op;
    }
}
