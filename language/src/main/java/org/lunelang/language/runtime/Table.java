package org.lunelang.language.runtime;

public final class Table {
    private Object metatable;
    private long length;
    private Object[] objectData;
    private byte[] primitiveData;

    public Object getMetatable() {
        return metatable;
    }

    public void setMetatable(Object metatable) {
        this.metatable = metatable;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Object getObjectData() {
        return objectData;
    }

    public void setObjectData(Object[] objectData) {
        this.objectData = objectData;
    }

    public byte[] getPrimitiveData() {
        return primitiveData;
    }

    public void setPrimitiveData(byte[] primitiveData) {
        this.primitiveData = primitiveData;
    }
}
