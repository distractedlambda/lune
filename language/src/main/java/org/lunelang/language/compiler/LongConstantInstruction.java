package org.lunelang.language.compiler;

public final class LongConstantInstruction extends Instruction {
    private long value;

    public LongConstantInstruction(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
