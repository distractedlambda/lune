package org.lunelang.language.runtime;

import com.oracle.truffle.api.memory.ByteArraySupport;

import static java.lang.Long.rotateLeft;
import static java.util.Objects.checkFromIndexSize;

// Adapted from https://github.com/cbreeden/fxhash
public final class FxHash {
    private FxHash() {}

    public static long combine(long hash, long input) {
        return (rotateLeft(hash, 5) ^ input) * 0x517cc1b727220a95L;
    }

    public static long combine(long hash, byte[] input, int offset, int size) {
        checkFromIndexSize(offset, size, input.length);

        while (size >= 8) {
            hash = combine(hash, BYTE_ARRAY_SUPPORT.getLong(input, offset));
            size -= 8;
            offset += 8;
        }

        if (size >= 4) {
            hash = combine(hash, Integer.toUnsignedLong(BYTE_ARRAY_SUPPORT.getInt(input, offset)));
            size -= 4;
            offset += 4;
        }

        if (size >= 2) {
            hash = combine(hash, Short.toUnsignedLong(BYTE_ARRAY_SUPPORT.getShort(input, offset)));
            size -= 2;
            offset += 2;
        }

        if (size >= 1) {
            hash = combine(hash, Byte.toUnsignedLong(input[offset]));
        }

        return hash;
    }

    public static long combine(long hash, byte[] input) {
        return combine(hash, input, 0, input.length);
    }

    public static long hash(byte[] input) {
        return combine(0, input);
    }

    private static final ByteArraySupport BYTE_ARRAY_SUPPORT = ByteArraySupport.littleEndian();
}
