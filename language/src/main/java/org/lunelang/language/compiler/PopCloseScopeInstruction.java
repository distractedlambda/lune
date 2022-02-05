package org.lunelang.language.compiler;

public final class PopCloseScopeInstruction extends Instruction {
    private PushCloseScopeInstruction pushInstruction;

    public PopCloseScopeInstruction(PushCloseScopeInstruction pushInstruction) {
        setPushInstruction(pushInstruction);
    }

    public PushCloseScopeInstruction getPushInstruction() {
        return pushInstruction;
    }

    public void setPushInstruction(PushCloseScopeInstruction pushInstruction) {
        this.pushInstruction = pushInstruction;
    }
}
