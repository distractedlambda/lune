package org.lunelang.language;

import com.oracle.truffle.api.memory.ByteArraySupport;

public final class Bytecode {
    private Bytecode() {}

    // [OP_EXECUTE instruction_index:int]
    public static final byte OP_EXECUTE = 0;

    // [OP_UNCONDITIONAL_BRANCH target:int]
    public static final byte OP_UNCONDITIONAL_BRANCH = 1;

    // [OP_CONDITIONAL_BRANCH target:int condition_profile:int condition_slot:int]
    public static final byte OP_CONDITIONAL_BRANCH = 2;

    // [OP_RETURN]
    public static final byte OP_RETURN = 3;

    // [OP_LOAD_FALSE slot:int]
    public static final byte OP_LOAD_FALSE = 4;

    // [OP_LOAD_TRUE slot:int]
    public static final byte OP_LOAD_TRUE = 5;

    // [OP_LOAD_LONG slot:int value:long]
    public static final byte OP_LOAD_LONG = 6;

    // [OP_LOAD_DOUBLE slot:int value:double]
    public static final byte OP_LOAD_DOUBLE = 7;

    // [OP_LOAD_NIL slot:int]
    public static final byte OP_LOAD_NIL = 8;

    // [OP_LOAD_OBJECT slot:int object:int]
    public static final byte OP_LOAD_OBJECT = 9;

    public static int getEmbeddedInt(byte[] bytecode, int offset) {
        return BYTE_ARRAY_SUPPORT.getInt(bytecode, offset);
    }

    public static long getEmbeddedLong(byte[] bytecode, int offset) {
        return BYTE_ARRAY_SUPPORT.getLong(bytecode, offset);
    }

    public static double getEmbeddedDouble(byte[] bytecode, int offset) {
        return BYTE_ARRAY_SUPPORT.getDouble(bytecode, offset);
    }

    private static final ByteArraySupport BYTE_ARRAY_SUPPORT = ByteArraySupport.littleEndian();
}
