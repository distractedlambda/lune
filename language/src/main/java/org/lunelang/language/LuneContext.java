package org.lunelang.language;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.utilities.AssumedValue;
import org.lunelang.language.runtime.Nil;

public final class LuneContext {
    private static final ContextReference<LuneContext> REFERENCE = ContextReference.create(LuneLanguage.class);

    private final AssumedValue<Object> nilMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> booleanMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> numberMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> stringMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> functionMetatable = new AssumedValue<>(Nil.getInstance());

    public static LuneContext get(Node node) {
        return REFERENCE.get(node);
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
