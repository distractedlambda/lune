package org.lunelang.language.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.utilities.TriState;
import org.lunelang.language.LuneLanguage;

import static org.lunelang.language.Todo.TODO;

@ExportLibrary(InteropLibrary.class)
public final class Closure extends DynamicObject {
    public Closure(Shape shape) {
        super(shape);
    }

    public CallTarget getCallTarget() {
        return (CallTarget) getShape().getDynamicType();
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
        return System.identityHashCode(this);
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    TriState isIdenticalOrUndefined(Object object) {
        return TriState.valueOf(this == object);
    }

    @ExportMessage
    Object execute(Object[] arguments) {
        throw TODO();
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        throw TODO();
    }
}
