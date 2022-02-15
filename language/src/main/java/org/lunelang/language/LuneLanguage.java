package org.lunelang.language;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import org.lunelang.language.runtime.InternedStringSet;

@TruffleLanguage.Registration(id = "lune", name = "Lune")
public final class LuneLanguage extends TruffleLanguage<LuneContext> {
    private static final LanguageReference<LuneLanguage> REFERENCE = LanguageReference.create(LuneLanguage.class);

    private final InternedStringSet internedStrings = new InternedStringSet();

    @CompilationFinal(dimensions = 1) private final byte[] nilDisplayString = internedStrings.intern("nil");
    @CompilationFinal(dimensions = 1) private final byte[] addMetamethodKey = internedStrings.intern("__add");
    @CompilationFinal(dimensions = 1) private final byte[] subMetamethodKey = internedStrings.intern("__sub");
    @CompilationFinal(dimensions = 1) private final byte[] mulMetamethodKey = internedStrings.intern("__mul");
    @CompilationFinal(dimensions = 1) private final byte[] divMetamethodKey = internedStrings.intern("__div");
    @CompilationFinal(dimensions = 1) private final byte[] modMetamethodKey = internedStrings.intern("__mod");
    @CompilationFinal(dimensions = 1) private final byte[] powMetamethodKey = internedStrings.intern("__pow");
    @CompilationFinal(dimensions = 1) private final byte[] unaryMinusMetamethodKey = internedStrings.intern("__unm");
    @CompilationFinal(dimensions = 1) private final byte[] integerDivideMetamethodKey = internedStrings.intern("__idiv");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseAndMetamethodKey = internedStrings.intern("__band");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseOrMetamethodKey = internedStrings.intern("__bor");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseExclusiveOrMetamethodKey = internedStrings.intern("__bxor");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseNotMetamethodKey = internedStrings.intern("__bnot");
    @CompilationFinal(dimensions = 1) private final byte[] shlMetamethodKey = internedStrings.intern("__shl");
    @CompilationFinal(dimensions = 1) private final byte[] shrMetamethodKey = internedStrings.intern("__shr");
    @CompilationFinal(dimensions = 1) private final byte[] concatMetamethodKey = internedStrings.intern("__concat");
    @CompilationFinal(dimensions = 1) private final byte[] lenMetamethodKey = internedStrings.intern("__len");
    @CompilationFinal(dimensions = 1) private final byte[] eqMetamethodKey = internedStrings.intern("__eq");
    @CompilationFinal(dimensions = 1) private final byte[] ltMetamethodKey = internedStrings.intern("__lt");
    @CompilationFinal(dimensions = 1) private final byte[] leMetamethodKey = internedStrings.intern("__le");
    @CompilationFinal(dimensions = 1) private final byte[] indexMetamethodKey = internedStrings.intern("__index");
    @CompilationFinal(dimensions = 1) private final byte[] newIndexMetamethodKey = internedStrings.intern("__newindex");
    @CompilationFinal(dimensions = 1) private final byte[] callMetamethodKey = internedStrings.intern("__call");
    @CompilationFinal(dimensions = 1) private final byte[] gcMetamethodKey = internedStrings.intern("__gc");
    @CompilationFinal(dimensions = 1) private final byte[] closeMetamethodKey = internedStrings.intern("__close");
    @CompilationFinal(dimensions = 1) private final byte[] modeMetavalueKey = internedStrings.intern("__mode");
    @CompilationFinal(dimensions = 1) private final byte[] nameMetavalueKey = internedStrings.intern("__name");
    @CompilationFinal(dimensions = 1) private final byte[] metatableMetavalueKey = internedStrings.intern("__metatable");

    @Override
    protected LuneContext createContext(Env env) {
        return new LuneContext();
    }

    public static LuneLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    public InternedStringSet getInternedStrings() {
        return internedStrings;
    }

    public byte[] getNilDisplayString() {
        return nilDisplayString;
    }

    public byte[] getAddMetamethodKey() {
        return addMetamethodKey;
    }

    public byte[] getSubMetamethodKey() {
        return subMetamethodKey;
    }

    public byte[] getMulMetamethodKey() {
        return mulMetamethodKey;
    }

    public byte[] getDivMetamethodKey() {
        return divMetamethodKey;
    }

    public byte[] getModMetamethodKey() {
        return modMetamethodKey;
    }

    public byte[] getPowMetamethodKey() {
        return powMetamethodKey;
    }

    public byte[] getUnaryMinusMetamethodKey() {
        return unaryMinusMetamethodKey;
    }

    public byte[] getIntegerDivideMetamethodKey() {
        return integerDivideMetamethodKey;
    }

    public byte[] getBitwiseAndMetamethodKey() {
        return bitwiseAndMetamethodKey;
    }

    public byte[] getBitwiseOrMetamethodKey() {
        return bitwiseOrMetamethodKey;
    }

    public byte[] getBitwiseExclusiveOrMetamethodKey() {
        return bitwiseExclusiveOrMetamethodKey;
    }

    public byte[] getBitwiseNotMetamethodKey() {
        return bitwiseNotMetamethodKey;
    }

    public byte[] getShlMetamethodKey() {
        return shlMetamethodKey;
    }

    public byte[] getShrMetamethodKey() {
        return shrMetamethodKey;
    }

    public byte[] getConcatMetamethodKey() {
        return concatMetamethodKey;
    }

    public byte[] getLenMetamethodKey() {
        return lenMetamethodKey;
    }

    public byte[] getEqMetamethodKey() {
        return eqMetamethodKey;
    }

    public byte[] getLtMetamethodKey() {
        return ltMetamethodKey;
    }

    public byte[] getLeMetamethodKey() {
        return leMetamethodKey;
    }

    public byte[] getIndexMetavalueKey() {
        return indexMetamethodKey;
    }

    public byte[] getNewIndexMetamethodKey() {
        return newIndexMetamethodKey;
    }

    public byte[] getCallMetamethodKey() {
        return callMetamethodKey;
    }

    public byte[] getGcMetamethodKey() {
        return gcMetamethodKey;
    }

    public byte[] getCloseMetamethodKey() {
        return closeMetamethodKey;
    }

    public byte[] getModeMetavalueKey() {
        return modeMetavalueKey;
    }

    public byte[] getNameMetavalueKey() {
        return nameMetavalueKey;
    }

    public byte[] getMetatableMetavalueKey() {
        return metatableMetavalueKey;
    }
}
