package org.lunelang.language.compiler;

public final class BooleanConstantInstruction extends Instruction {
    private boolean value;

    public BooleanConstantInstruction(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
