package org.lunelang.language.compiler;

public final class PushCloseScopeInstruction extends Instruction {
    private LocalVariable localVariable;

    public PushCloseScopeInstruction(LocalVariable localVariable) {
        setLocalVariable(localVariable);
    }

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void setLocalVariable(LocalVariable localVariable) {
        this.localVariable = localVariable;
    }
}
