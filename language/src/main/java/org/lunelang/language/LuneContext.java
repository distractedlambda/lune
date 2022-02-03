package org.lunelang.language;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.utilities.AssumedValue;
import org.lunelang.language.runtime.InternedStringSet;
import org.lunelang.language.runtime.LuneString;
import org.lunelang.language.runtime.Nil;

public final class LuneContext {
    private static final ContextReference<LuneContext> REFERENCE = ContextReference.create(LuneLanguage.class);

    private final InternedStringSet internedStrings = new InternedStringSet();

    private final LuneString addMetamethodKey = internedStrings.intern("__add");
    private final LuneString subMetamethodKey = internedStrings.intern("__sub");
    private final LuneString mulMetamethodKey = internedStrings.intern("__mul");
    private final LuneString divMetamethodKey = internedStrings.intern("__div");
    private final LuneString modMetamethodKey = internedStrings.intern("__mod");
    private final LuneString powMetamethodKey = internedStrings.intern("__pow");
    private final LuneString unaryMinusMetamethodKey = internedStrings.intern("__unm");
    private final LuneString integerDivideMetamethodKey = internedStrings.intern("__idiv");
    private final LuneString bitwiseAndMetamethodKey = internedStrings.intern("__band");
    private final LuneString bitwiseOrMetamethodKey = internedStrings.intern("__bor");
    private final LuneString bitwiseExclusiveOrMetamethodKey = internedStrings.intern("__bxor");
    private final LuneString bitwiseNotMetamethodKey = internedStrings.intern("__bnot");
    private final LuneString shlMetamethodKey = internedStrings.intern("__shl");
    private final LuneString shrMetamethodKey = internedStrings.intern("__shr");
    private final LuneString concatMetamethodKey = internedStrings.intern("__concat");
    private final LuneString lenMetamethodKey = internedStrings.intern("__len");
    private final LuneString eqMetamethodKey = internedStrings.intern("__eq");
    private final LuneString ltMetamethodKey = internedStrings.intern("__lt");
    private final LuneString leMetamethodKey = internedStrings.intern("__le");
    private final LuneString indexMetamethodKey = internedStrings.intern("__index");
    private final LuneString newIndexMetamethodKey = internedStrings.intern("__newindex");
    private final LuneString callMetamethodKey = internedStrings.intern("__call");
    private final LuneString gcMetamethodKey = internedStrings.intern("__gc");
    private final LuneString closeMetamethodKey = internedStrings.intern("__close");
    private final LuneString modeMetavalueKey = internedStrings.intern("__mode");
    private final LuneString nameMetavalueKey = internedStrings.intern("__name");

    private final AssumedValue<Object> nilMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> booleanMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> numberMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> stringMetatable = new AssumedValue<>(Nil.getInstance());
    private final AssumedValue<Object> functionMetatable = new AssumedValue<>(Nil.getInstance());

    public static LuneContext get(Node node) {
        return REFERENCE.get(node);
    }

    public InternedStringSet getInternedStrings() {
        return internedStrings;
    }

    public LuneString getAddMetamethodKey() {
        return addMetamethodKey;
    }

    public LuneString getSubMetamethodKey() {
        return subMetamethodKey;
    }

    public LuneString getMulMetamethodKey() {
        return mulMetamethodKey;
    }

    public LuneString getDivMetamethodKey() {
        return divMetamethodKey;
    }

    public LuneString getModMetamethodKey() {
        return modMetamethodKey;
    }

    public LuneString getPowMetamethodKey() {
        return powMetamethodKey;
    }

    public LuneString getUnaryMinusMetamethodKey() {
        return unaryMinusMetamethodKey;
    }

    public LuneString getIntegerDivideMetamethodKey() {
        return integerDivideMetamethodKey;
    }

    public LuneString getBitwiseAndMetamethodKey() {
        return bitwiseAndMetamethodKey;
    }

    public LuneString getBitwiseOrMetamethodKey() {
        return bitwiseOrMetamethodKey;
    }

    public LuneString getBitwiseExclusiveOrMetamethodKey() {
        return bitwiseExclusiveOrMetamethodKey;
    }

    public LuneString getBitwiseNotMetamethodKey() {
        return bitwiseNotMetamethodKey;
    }

    public LuneString getShlMetamethodKey() {
        return shlMetamethodKey;
    }

    public LuneString getShrMetamethodKey() {
        return shrMetamethodKey;
    }

    public LuneString getConcatMetamethodKey() {
        return concatMetamethodKey;
    }

    public LuneString getLenMetamethodKey() {
        return lenMetamethodKey;
    }

    public LuneString getEqMetamethodKey() {
        return eqMetamethodKey;
    }

    public LuneString getLtMetamethodKey() {
        return ltMetamethodKey;
    }

    public LuneString getLeMetamethodKey() {
        return leMetamethodKey;
    }

    public LuneString getIndexMetavalueKey() {
        return indexMetamethodKey;
    }

    public LuneString getNewIndexMetamethodKey() {
        return newIndexMetamethodKey;
    }

    public LuneString getCallMetamethodKey() {
        return callMetamethodKey;
    }

    public LuneString getGcMetamethodKey() {
        return gcMetamethodKey;
    }

    public LuneString getCloseMetamethodKey() {
        return closeMetamethodKey;
    }

    public LuneString getModeMetavalueKey() {
        return modeMetavalueKey;
    }

    public LuneString getNameMetavalueKey() {
        return nameMetavalueKey;
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
