package org.lunelang.language.runtime;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import org.lunelang.language.LuneLanguage;

import static org.lunelang.language.Todo.TODO;
import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

@ExportLibrary(InteropLibrary.class)
public final class LuneError extends AbstractTruffleException {
    private final Object messageObject;
    private long level;

    public LuneError(Object messageObject, long level, Node location) {
        super(location);
        assert messageObject != null;
        this.messageObject = messageObject;
        this.level = level;
    }

    public LuneError(Object messageObject, long level) {
        this(messageObject, level, null);
    }

    public Object getMessageObject() {
        return messageObject;
    }

    public long getLevel() {
        return level;
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
    boolean hasExceptionMessage() {
        return !isNil(messageObject);
    }

    @ExportMessage
    boolean hasExceptionStackTrace() {
        return level != 0;
    }

    @ExportMessage
    boolean isException() {
        return true;
    }

    @ExportMessage
    RuntimeException throwException() {
        throw TODO();
    }

    @ExportMessage
    LuneStringWrapper getExceptionMessage() {
        throw TODO();
    }

    @ExportMessage
    Object getExceptionStackTrace() {
        throw TODO();
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        throw TODO();
    }
}
