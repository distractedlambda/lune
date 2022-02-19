package org.lunelang.language;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import org.lunelang.language.runtime.FxHash;
import org.lunelang.language.runtime.InternedSet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@TruffleLanguage.Registration(id = "lune", name = "Lune")
public final class LuneLanguage extends TruffleLanguage<LuneContext> {
    private static final LanguageReference<LuneLanguage> REFERENCE = LanguageReference.create(LuneLanguage.class);

    private final InternedSet<byte[]> internedStrings = new InternedSet<>(
        byte[].class,
        new InternedSet.Equivalence<>() {
            @Override
            public boolean equals(byte[] lhs, byte[] rhs) {
                return Arrays.equals(lhs, rhs);
            }

            @Override
            public int hashCode(byte[] element) {
                return (int) FxHash.hash(element);
            }
        }
    );

    @CompilationFinal(dimensions = 1) private final byte[] nilDisplayString = intern("nil");
    @CompilationFinal(dimensions = 1) private final byte[] addMetamethodKey = intern("__add");
    @CompilationFinal(dimensions = 1) private final byte[] subMetamethodKey = intern("__sub");
    @CompilationFinal(dimensions = 1) private final byte[] mulMetamethodKey = intern("__mul");
    @CompilationFinal(dimensions = 1) private final byte[] divMetamethodKey = intern("__div");
    @CompilationFinal(dimensions = 1) private final byte[] modMetamethodKey = intern("__mod");
    @CompilationFinal(dimensions = 1) private final byte[] powMetamethodKey = intern("__pow");
    @CompilationFinal(dimensions = 1) private final byte[] unaryMinusMetamethodKey = intern("__unm");
    @CompilationFinal(dimensions = 1) private final byte[] integerDivideMetamethodKey = intern("__idiv");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseAndMetamethodKey = intern("__band");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseOrMetamethodKey = intern("__bor");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseExclusiveOrMetamethodKey = intern("__bxor");
    @CompilationFinal(dimensions = 1) private final byte[] bitwiseNotMetamethodKey = intern("__bnot");
    @CompilationFinal(dimensions = 1) private final byte[] shlMetamethodKey = intern("__shl");
    @CompilationFinal(dimensions = 1) private final byte[] shrMetamethodKey = intern("__shr");
    @CompilationFinal(dimensions = 1) private final byte[] concatMetamethodKey = intern("__concat");
    @CompilationFinal(dimensions = 1) private final byte[] lenMetamethodKey = intern("__len");
    @CompilationFinal(dimensions = 1) private final byte[] eqMetamethodKey = intern("__eq");
    @CompilationFinal(dimensions = 1) private final byte[] ltMetamethodKey = intern("__lt");
    @CompilationFinal(dimensions = 1) private final byte[] leMetamethodKey = intern("__le");
    @CompilationFinal(dimensions = 1) private final byte[] indexMetamethodKey = intern("__index");
    @CompilationFinal(dimensions = 1) private final byte[] newIndexMetamethodKey = intern("__newindex");
    @CompilationFinal(dimensions = 1) private final byte[] callMetamethodKey = intern("__call");
    @CompilationFinal(dimensions = 1) private final byte[] gcMetamethodKey = intern("__gc");
    @CompilationFinal(dimensions = 1) private final byte[] closeMetamethodKey = intern("__close");
    @CompilationFinal(dimensions = 1) private final byte[] modeMetavalueKey = intern("__mode");
    @CompilationFinal(dimensions = 1) private final byte[] nameMetavalueKey = intern("__name");
    @CompilationFinal(dimensions = 1) private final byte[] metatableMetavalueKey = intern("__metatable");

    @Override
    protected LuneContext createContext(Env env) {
        return new LuneContext();
    }

    public static LuneLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    public InternedSet<byte[]> getInternedStrings() {
        return internedStrings;
    }

    public byte[] intern(String string) {
        return internBoundary(internedStrings, string);
    }

    @TruffleBoundary
    private static byte[] internBoundary(InternedSet<byte[]> internedStrings, String string) {
        return internedStrings.intern(string.getBytes(StandardCharsets.UTF_8));
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
