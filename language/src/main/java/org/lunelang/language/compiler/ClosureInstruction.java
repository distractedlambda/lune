package org.lunelang.language.compiler;

public final class ClosureInstruction extends Instruction {
    private Function function;

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
