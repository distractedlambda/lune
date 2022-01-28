package org.lunelang.language.nodes;

public final class Bytecode {
    private Bytecode() {}

    public static final byte OP_INSTRUCTION = 0;
    public static final byte OP_UNCONDITIONAL_BRANCH = 1;
    public static final byte OP_CONDITIONAL_BRANCH = 2;
    public static final byte OP_RETURN = 3;

    public static int getEmbeddedInt(byte[] bytecode, int offset) {
        return (bytecode[offset+3] << 24) | (bytecode[offset+2] << 16) | (bytecode[offset+1] << 8) | bytecode[offset];
    }
}
