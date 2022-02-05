package org.lunelang.language.compiler;

import com.oracle.truffle.api.memory.ByteArraySupport;

import java.util.Arrays;

import static java.lang.Math.addExact;
import static java.lang.Math.max;
import static java.util.Objects.checkFromIndexSize;
import static java.util.Objects.checkIndex;

public final class ByteVector {
    private static final ByteArraySupport BYTE_ARRAY_SUPPORT = ByteArraySupport.littleEndian();
    private static final byte[] EMPTY_BUFFER = new byte[0];

    private byte[] buffer = EMPTY_BUFFER;
    private int size = 0;

    public int getSize() {
        return size;
    }

    public byte getByte(int byteOffset) {
        checkIndex(byteOffset, size);
        return buffer[byteOffset];
    }

    public short getShort(int byteOffset) {
        checkFromIndexSize(byteOffset, 2, size);
        return BYTE_ARRAY_SUPPORT.getShort(buffer, byteOffset);
    }

    public int getInt(int byteOffset) {
        checkFromIndexSize(byteOffset, 4, size);
        return BYTE_ARRAY_SUPPORT.getInt(buffer, byteOffset);
    }

    public long getLong(int byteOffset) {
        checkFromIndexSize(byteOffset, 8, size);
        return BYTE_ARRAY_SUPPORT.getLong(buffer, byteOffset);
    }

    public float getFloat(int byteOffset) {
        checkFromIndexSize(byteOffset, 4, size);
        return BYTE_ARRAY_SUPPORT.getFloat(buffer, byteOffset);
    }

    public double getDouble(int byteOffset) {
        checkFromIndexSize(byteOffset, 8, size);
        return BYTE_ARRAY_SUPPORT.getDouble(buffer, byteOffset);
    }

    public void setByte(int byteOffset, byte value) {
        checkIndex(byteOffset, size);
        buffer[byteOffset] = value;
    }

    public void setShort(int byteOffset, short value) {
        checkFromIndexSize(byteOffset, 2, size);
        BYTE_ARRAY_SUPPORT.putShort(buffer, byteOffset, value);
    }

    public void setInt(int byteOffset, int value) {
        checkFromIndexSize(byteOffset, 4, size);
        BYTE_ARRAY_SUPPORT.putInt(buffer, byteOffset, value);
    }

    public void setLong(int byteOffset, long value) {
        checkFromIndexSize(byteOffset, 8, size);
        BYTE_ARRAY_SUPPORT.putLong(buffer, byteOffset, value);
    }

    public void setFloat(int byteOffset, float value) {
        checkFromIndexSize(byteOffset, 4, size);
        BYTE_ARRAY_SUPPORT.putFloat(buffer, byteOffset, value);
    }

    public void setDouble(int byteOffset, double value) {
        checkFromIndexSize(byteOffset, 8, size);
        BYTE_ARRAY_SUPPORT.putDouble(buffer, byteOffset, value);
    }

    private void growIfNeeded(int additionalCapacity) {
        var minimumCapacity = addExact(size, additionalCapacity);

        if (buffer.length > minimumCapacity) {
            return;
        }

        var newCapacity = max(buffer.length + buffer.length / 2, minimumCapacity);
        buffer = Arrays.copyOf(buffer, newCapacity);
    }

    public void appendByte(byte value) {
        growIfNeeded(1);
        buffer[size++] = value;
    }

    public void appendShort(short value) {
        growIfNeeded(2);
        BYTE_ARRAY_SUPPORT.putShort(buffer, size, value);
        size += 2;
    }

    public void appendInt(int value) {
        growIfNeeded(4);
        BYTE_ARRAY_SUPPORT.putInt(buffer, size, value);
        size += 4;
    }

    public void appendLong(long value) {
        growIfNeeded(8);
        BYTE_ARRAY_SUPPORT.putLong(buffer, size, value);
        size += 8;
    }

    public void appendFloat(float value) {
        growIfNeeded(4);
        BYTE_ARRAY_SUPPORT.putFloat(buffer, size, value);
        size += 4;
    }

    public void appendDouble(double value) {
        growIfNeeded(8);
        BYTE_ARRAY_SUPPORT.putDouble(buffer, size, value);
        size += 8;
    }

    public void appendUTF8(int codePoint) {
        if (codePoint < 0) {
            throw new IllegalArgumentException("Illegal Unicode scalar value: " + codePoint);
        } else if (codePoint < 0x80) {
            appendByte((byte) codePoint);
        } else if (codePoint < 0x800) {
            growIfNeeded(2);
            buffer[size] = (byte) (0xc0 | (codePoint >> 6));
            buffer[size + 1] = (byte) (0x80 | (codePoint & 0x3f));
            size += 2;
        } else if (codePoint < 0x10000) {
            growIfNeeded(3);
            buffer[size] = (byte) (0xe0 | (codePoint >> 12));
            buffer[size + 1] = (byte) (0x80 | ((codePoint >> 6) & 0x3f));
            buffer[size + 2] = (byte) (0x80 | (codePoint & 0x3f));
            size += 3;
        } else if (codePoint < 0x200000) {
            growIfNeeded(4);
            buffer[size] = (byte) (0xf0 | (codePoint >> 18));
            buffer[size + 1] = (byte) (0x80 | ((codePoint >> 12) & 0x3f));
            buffer[size + 2] = (byte) (0x80 | ((codePoint >> 6) & 0x3f));
            buffer[size + 3] = (byte) (0x80 | (codePoint & 0x3f));
            size += 4;
        } else if (codePoint < 0x4000000) {
            growIfNeeded(5);
            buffer[size] = (byte) (0xf8 | (codePoint >> 24));
            buffer[size + 1] = (byte) (0x80 | ((codePoint >> 18) & 0x3f));
            buffer[size + 2] = (byte) (0x80 | ((codePoint >> 12) & 0x3f));
            buffer[size + 3] = (byte) (0x80 | ((codePoint >> 6) & 0x3f));
            buffer[size + 4] = (byte) (0x80 | (codePoint & 0x3f));
            size += 5;
        } else {
            growIfNeeded(6);
            buffer[size] = (byte) (0xfc | (codePoint >> 30));
            buffer[size + 1] = (byte) (0x80 | ((codePoint >> 24) & 0x3f));
            buffer[size + 2] = (byte) (0x80 | ((codePoint >> 18) & 0x3f));
            buffer[size + 3] = (byte) (0x80 | ((codePoint >> 12) & 0x3f));
            buffer[size + 4] = (byte) (0x80 | ((codePoint >> 6) & 0x3f));
            buffer[size + 5] = (byte) (0x80 | (codePoint & 0x3f));
            size += 6;
        }
    }

    public void appendUTF8(CharSequence charSequence) {
        charSequence.codePoints().forEach(this::appendUTF8);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buffer, size);
    }
}
