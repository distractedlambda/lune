package org.lunelang.language.compiler;

public final class ExtractScalarInstruction extends Instruction {
    private Instruction vector;
    private int index;

    public ExtractScalarInstruction(Instruction vector, int index) {
        setVector(vector);
        setIndex(index);
    }

    public Instruction getVector() {
        return vector;
    }

    public void setVector(Instruction vector) {
        this.vector = vector;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
