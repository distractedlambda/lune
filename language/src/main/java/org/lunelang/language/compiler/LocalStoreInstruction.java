package org.lunelang.language.compiler;

public final class LocalStoreInstruction extends Instruction {
    private LocalVariable localVariable;
    private Instruction value;

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void setLocalVariable(LocalVariable localVariable) {
        if (this.localVariable != null) {
            this.localVariable.removeStore(this);
        }

        if (localVariable != null) {
            localVariable.addStore(this);
        }

        this.localVariable = localVariable;
    }

    public Instruction getValue() {
        return value;
    }

    public void setValue(Instruction value) {
        this.value = value;
    }
}
