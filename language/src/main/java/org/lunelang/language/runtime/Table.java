package org.lunelang.language.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.HiddenKey;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.utilities.TriState;
import org.lunelang.language.LuneLanguage;

import static com.oracle.truffle.api.CompilerDirectives.castExact;
import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;
import static org.lunelang.language.Todo.TODO;

@ExportLibrary(InteropLibrary.class)
public final class Table extends DynamicObject {
    public Table(Shape shape) {
        super(shape);
    }

    public Object getMetatable() {
        return getShape().getDynamicType();
    }

    public Object getMetatable(DynamicObjectLibrary tables) {
        return tables.getDynamicType(this);
    }

    public long getSequenceLength(DynamicObjectLibrary tables) {
        try {
            return tables.getLongOrDefault(this, SEQUENCE_LENGTH_KEY, 0L);
        } catch (UnexpectedResultException exception) {
            throw shouldNotReachHere(exception);
        }
    }

    public Object[] getSequenceStorage(DynamicObjectLibrary tables) {
        return castExact(tables.getOrDefault(this, SEQUENCE_STORAGE_KEY, null), Object[].class);
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
    long getArraySize() {
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

    private static final HiddenKey SEQUENCE_LENGTH_KEY = new HiddenKey("sequence length");
    private static final HiddenKey SEQUENCE_STORAGE_KEY = new HiddenKey("sequence storage");
}
