package org.lunelang.language;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.utilities.AssumedValue;
import org.lunelang.language.runtime.InternedStringSet;
import org.lunelang.language.runtime.Nil;

import java.nio.charset.StandardCharsets;

public final class LuneContext {
    private static final ContextReference<LuneContext> REFERENCE = ContextReference.create(LuneLanguage.class);

    private final InternedStringSet internedStrings = new InternedStringSet();

    private final AssumedValue<Object> nilMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> booleanMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> numberMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> stringMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> functionMetatable = new AssumedValue<>(Nil.getInstance());

    public static LuneContext get(Node node) {
        return REFERENCE.get(node);
    }

    @TruffleBoundary
    public byte[] intern(byte[] string) {
        return internedStrings.intern(string);
    }

    @TruffleBoundary
    public byte[] intern(String string) {
        return internedStrings.intern(string.getBytes(StandardCharsets.UTF_8));
    }

    public Object getNilMetatable() {
        return nilMetatable.get();
    }

    public void setNilMetatable(Object value) {
        nilMetatable.set(value);
    }

    public Object getBooleanMetatable() {
        return booleanMetatable.get();
    }

    public void setBooleanMetatable(Object value) {
        booleanMetatable.set(value);
    }

    public Object getNumberMetatable() {
        return numberMetatable.get();
    }

    public void setNumberMetatable(Object value) {
        numberMetatable.set(value);
    }

    public Object getStringMetatable() {
        return stringMetatable.get();
    }

    public void setStringMetatable(Object value) {
        stringMetatable.set(value);
    }

    public Object getFunctionMetatable() {
        return functionMetatable.get();
    }

    public void setFunctionMetatable(Object value) {
        functionMetatable.set(value);
    }
}
