package org.lunelang.language.compiler;

public final class LocalStoreInstruction extends Instruction {
    private LocalVariable localVariable;
    private Instruction value;

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void setLocalVariable(LocalVariable localVariable) {
        this.localVariable = localVariable;
    }

    public Instruction getValue() {
        return value;
    }

    public void setValue(Instruction value) {
        this.value = value;
    }
}
