package org.lunelang.language.compiler;

public final class LocalLoadInstruction extends Instruction {
    private LocalVariable localVariable;

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void setLocalVariable(LocalVariable localVariable) {
        this.localVariable = localVariable;
    }
}
