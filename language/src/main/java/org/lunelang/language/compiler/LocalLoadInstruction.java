package org.lunelang.language.compiler;

public final class LocalLoadInstruction extends Instruction {
    private LocalVariable localVariable;

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void setLocalVariable(LocalVariable localVariable) {
        if (this.localVariable != null) {
            this.localVariable.removeLoad(this);
        }

        if (localVariable != null) {
            localVariable.addLoad(this);
        }

        this.localVariable = localVariable;
    }
}
