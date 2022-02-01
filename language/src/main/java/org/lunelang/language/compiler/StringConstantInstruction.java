package org.lunelang.language.compiler;

public final class StringConstantInstruction extends Instruction {
    private byte[] bytes;

    public StringConstantInstruction(byte[] bytes) {
        setBytes(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
