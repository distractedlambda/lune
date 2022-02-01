package org.lunelang.language.compiler;

public final class BinaryOpInstruction extends Instruction {
    BinaryOp op;
    Instruction lhs, rhs;

    public BinaryOpInstruction(BinaryOp op, Instruction lhs, Instruction rhs) {
        setOp(op);
        setLhs(lhs);
        setRhs(rhs);
    }

    public BinaryOp getOp() {
        return op;
    }

    public void setOp(BinaryOp op) {
        this.op = op;
    }

    public Instruction getLhs() {
        return lhs;
    }

    public void setLhs(Instruction lhs) {
        this.lhs = lhs;
    }

    public Instruction getRhs() {
        return rhs;
    }

    public void setRhs(Instruction rhs) {
        this.rhs = rhs;
    }
}
