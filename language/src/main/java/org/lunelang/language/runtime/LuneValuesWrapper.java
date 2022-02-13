package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import org.lunelang.language.LuneLanguage;

import static org.lunelang.language.Todo.TODO;

@ValueType
@ExportLibrary(InteropLibrary.class)
public final class LuneValuesWrapper implements TruffleObject {
    @CompilationFinal(dimensions = 1) private final Object[] values;

    public LuneValuesWrapper(Object[] values) {
        assert values != null;
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    @ExportMessage
    long getArraySize() {
        return values.length;
    }

    @ExportMessage
    Class<LuneLanguage> getLanguage() {
        return LuneLanguage.class;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (index >= 0 && index < values.length) {
            return values[(int) index];
        } else {
            throw InvalidArrayIndexException.create(index);
        }
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < values.length;
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        throw TODO();
    }

    @Override
    public int hashCode() {
        throw TODO();
    }

    @Override
    public boolean equals(Object object) {
        throw TODO();
    }

    @Override
    public String toString() {
        throw TODO();
    }
}
