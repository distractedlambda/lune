package org.lunelang.language.runtime;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

public final class CaptureBox {
    private byte type;
    private long primitiveValue;
    private Object objectValue;

    private static final byte TYPE_NIL = 0;
    private static final byte TYPE_BOOLEAN = 1;
    private static final byte TYPE_LONG = 2;
    private static final byte TYPE_DOUBLE = 3;
    private static final byte TYPE_OBJECT = 4;

    public boolean isNil() {
        return type == TYPE_NIL;
    }

    public boolean isBoolean() {
        return type == TYPE_BOOLEAN;
    }

    public boolean isLong() {
        return type == TYPE_LONG;
    }

    public boolean isDouble() {
        return type == TYPE_DOUBLE;
    }

    public boolean isObject() {
        return type == TYPE_OBJECT;
    }

    public boolean getBoolean() {
        assert type == TYPE_BOOLEAN;
        return primitiveValue != 0;
    }

    public long getLong() {
        assert type == TYPE_LONG;
        return primitiveValue;
    }

    public double getDouble() {
        assert type == TYPE_DOUBLE;
        return longBitsToDouble(primitiveValue);
    }

    public Object getObject() {
        assert type == TYPE_OBJECT;
        return objectValue;
    }

    public void setNil() {
        type = TYPE_NIL;
        objectValue = null;
    }

    public void setBoolean(boolean value) {
        type = TYPE_BOOLEAN;
        primitiveValue = value ? 1 : 0;
        objectValue = null;
    }

    public void setLong(long value) {
        type = TYPE_LONG;
        primitiveValue = value;
        objectValue = null;
    }

    public void setDouble(double value) {
        type = TYPE_DOUBLE;
        primitiveValue = doubleToRawLongBits(value);
        objectValue = null;
    }

    public void setObject(Object value) {
        type = TYPE_OBJECT;
        objectValue = value;
    }
}
