package org.lunelang.language;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import org.lunelang.language.runtime.InternedString;
import org.lunelang.language.runtime.InternedStringSet;

@TruffleLanguage.Registration(id = "lune", name = "Lune")
public final class LuneLanguage extends TruffleLanguage<LuneContext> {
    private static final LanguageReference<LuneLanguage> REFERENCE = LanguageReference.create(LuneLanguage.class);

    private final InternedStringSet internedStrings = new InternedStringSet();

    private final InternedString addMetamethodKey = internedStrings.intern("__add");
    private final InternedString subMetamethodKey = internedStrings.intern("__sub");
    private final InternedString mulMetamethodKey = internedStrings.intern("__mul");
    private final InternedString divMetamethodKey = internedStrings.intern("__div");
    private final InternedString modMetamethodKey = internedStrings.intern("__mod");
    private final InternedString powMetamethodKey = internedStrings.intern("__pow");
    private final InternedString unaryMinusMetamethodKey = internedStrings.intern("__unm");
    private final InternedString integerDivideMetamethodKey = internedStrings.intern("__idiv");
    private final InternedString bitwiseAndMetamethodKey = internedStrings.intern("__band");
    private final InternedString bitwiseOrMetamethodKey = internedStrings.intern("__bor");
    private final InternedString bitwiseExclusiveOrMetamethodKey = internedStrings.intern("__bxor");
    private final InternedString bitwiseNotMetamethodKey = internedStrings.intern("__bnot");
    private final InternedString shlMetamethodKey = internedStrings.intern("__shl");
    private final InternedString shrMetamethodKey = internedStrings.intern("__shr");
    private final InternedString concatMetamethodKey = internedStrings.intern("__concat");
    private final InternedString lenMetamethodKey = internedStrings.intern("__len");
    private final InternedString eqMetamethodKey = internedStrings.intern("__eq");
    private final InternedString ltMetamethodKey = internedStrings.intern("__lt");
    private final InternedString leMetamethodKey = internedStrings.intern("__le");
    private final InternedString indexMetamethodKey = internedStrings.intern("__index");
    private final InternedString newIndexMetamethodKey = internedStrings.intern("__newindex");
    private final InternedString callMetamethodKey = internedStrings.intern("__call");
    private final InternedString gcMetamethodKey = internedStrings.intern("__gc");
    private final InternedString closeMetamethodKey = internedStrings.intern("__close");
    private final InternedString modeMetavalueKey = internedStrings.intern("__mode");
    private final InternedString nameMetavalueKey = internedStrings.intern("__name");

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

    public InternedString getAddMetamethodKey() {
        return addMetamethodKey;
    }

    public InternedString getSubMetamethodKey() {
        return subMetamethodKey;
    }

    public InternedString getMulMetamethodKey() {
        return mulMetamethodKey;
    }

    public InternedString getDivMetamethodKey() {
        return divMetamethodKey;
    }

    public InternedString getModMetamethodKey() {
        return modMetamethodKey;
    }

    public InternedString getPowMetamethodKey() {
        return powMetamethodKey;
    }

    public InternedString getUnaryMinusMetamethodKey() {
        return unaryMinusMetamethodKey;
    }

    public InternedString getIntegerDivideMetamethodKey() {
        return integerDivideMetamethodKey;
    }

    public InternedString getBitwiseAndMetamethodKey() {
        return bitwiseAndMetamethodKey;
    }

    public InternedString getBitwiseOrMetamethodKey() {
        return bitwiseOrMetamethodKey;
    }

    public InternedString getBitwiseExclusiveOrMetamethodKey() {
        return bitwiseExclusiveOrMetamethodKey;
    }

    public InternedString getBitwiseNotMetamethodKey() {
        return bitwiseNotMetamethodKey;
    }

    public InternedString getShlMetamethodKey() {
        return shlMetamethodKey;
    }

    public InternedString getShrMetamethodKey() {
        return shrMetamethodKey;
    }

    public InternedString getConcatMetamethodKey() {
        return concatMetamethodKey;
    }

    public InternedString getLenMetamethodKey() {
        return lenMetamethodKey;
    }

    public InternedString getEqMetamethodKey() {
        return eqMetamethodKey;
    }

    public InternedString getLtMetamethodKey() {
        return ltMetamethodKey;
    }

    public InternedString getLeMetamethodKey() {
        return leMetamethodKey;
    }

    public InternedString getIndexMetavalueKey() {
        return indexMetamethodKey;
    }

    public InternedString getNewIndexMetamethodKey() {
        return newIndexMetamethodKey;
    }

    public InternedString getCallMetamethodKey() {
        return callMetamethodKey;
    }

    public InternedString getGcMetamethodKey() {
        return gcMetamethodKey;
    }

    public InternedString getCloseMetamethodKey() {
        return closeMetamethodKey;
    }

    public InternedString getModeMetavalueKey() {
        return modeMetavalueKey;
    }

    public InternedString getNameMetavalueKey() {
        return nameMetavalueKey;
    }

}
