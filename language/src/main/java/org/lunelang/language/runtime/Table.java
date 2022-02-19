package org.lunelang.language.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.TriState;
import org.lunelang.language.LuneLanguage;

import static org.lunelang.language.Todo.TODO;

@ExportLibrary(InteropLibrary.class)
public final class Table implements TruffleObject {
    private static final Object[] EMPTY_OBJECT_SLOTS = new Object[0];
    private static final long[] EMPTY_PRIMITIVE_SLOTS = new long[0];

    private Object metatable = Nil.getInstance();
    private Object[] objectSlots = EMPTY_OBJECT_SLOTS;
    private long[] primitiveSlots = EMPTY_PRIMITIVE_SLOTS;
    private Object arrayStorage = null;
    private long arraySize = 0;

    public Object getMetatable() {
        return metatable;
    }

    public void setMetatable(Object metatable) {
        assert metatable != null;
        this.metatable = metatable;
    }

    public Object[] getObjectSlots() {
        return objectSlots;
    }

    public void setObjectSlots(Object[] objectSlots) {
        assert objectSlots != null;
        this.objectSlots = objectSlots;
    }

    public long[] getPrimitiveSlots() {
        return primitiveSlots;
    }

    public void setPrimitiveSlots(long[] primitiveSlots) {
        assert primitiveSlots != null;
        this.primitiveSlots = primitiveSlots;
    }

    public Object getArrayStorage() {
        return arrayStorage;
    }

    public void setArrayStorage(Object arrayStorage) {
        this.arrayStorage = arrayStorage;
    }

    @ExportMessage.Ignore
    public long getArraySize() {
        throw TODO();
    }

    public void setArraySize(long arraySize) {
        this.arraySize = arraySize;
    }

    @ExportMessage(name = "getArraySize")
    long getInteropArraySize() {
        return arraySize;
    }

    @ExportMessage
    Class<LuneLanguage> getLanguage() {
        return LuneLanguage.class;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    boolean hasHashEntries() {
        return true;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    int identityHashCode() {
        return System.identityHashCode(this);
    }

    @ExportMessage
    TriState isIdenticalOrUndefined(Object object) {
        return TriState.valueOf(this == object);
    }

    @ExportMessage
    Object getMembers(boolean includeInternal) {
        throw TODO();
    }

    @ExportMessage
    long getHashSize() {
        throw TODO();
    }

    @ExportMessage
    Object getHashEntriesIterator() {
        throw TODO();
    }

    @ExportMessage
    Object readArrayElement(long index) {
        throw TODO();
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        throw TODO();
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        throw TODO();
    }
}
