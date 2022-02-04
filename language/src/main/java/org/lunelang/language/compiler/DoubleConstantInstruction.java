package org.lunelang.language.compiler;

public final class DoubleConstantInstruction extends Instruction {
    private double value;

    public DoubleConstantInstruction(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
