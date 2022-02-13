package org.lunelang.language.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.TriState;
import org.lunelang.language.LuneLanguage;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

@ExportLibrary(InteropLibrary.class)
public final class Nil implements TruffleObject {
    private Nil() {}

    private static final Nil INSTANCE = new Nil();

    private static final int HASH_CODE = System.identityHashCode(INSTANCE);

    public static Nil getInstance() {
        return INSTANCE;
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
    int identityHashCode() {
        return HASH_CODE;
    }

    @ExportMessage
    TriState isIdenticalOrUndefined(Object object) {
        return TriState.valueOf(isNil(object));
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        return new LuneStringWrapper(LuneLanguage.get(null).getNilDisplayString());
    }

    @Override
    public boolean equals(Object object) {
        return object == INSTANCE;
    }

    @Override
    public int hashCode() {
        return HASH_CODE;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
