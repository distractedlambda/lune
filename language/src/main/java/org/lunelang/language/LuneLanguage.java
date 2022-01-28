package org.lunelang.language;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import org.lunelang.language.runtime.InternedBoolean;
import org.lunelang.language.runtime.InternedDouble;
import org.lunelang.language.runtime.InternedLong;
import org.lunelang.language.runtime.InternedString;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TruffleLanguage.Registration(id = "lune", name = "Lune")
public final class LuneLanguage extends TruffleLanguage<LuneContext> {
    private static final LanguageReference<LuneLanguage> REFERENCE = LanguageReference.create(LuneLanguage.class);

    private record InternedStringKey(byte[] bytes) {
        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof InternedStringKey other)) {
                return false;
            }

            return Arrays.equals(bytes, other.bytes);
        }
    }

    private final ConcurrentMap<InternedStringKey, InternedString> internedStrings = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, InternedLong> internedLongs = new ConcurrentHashMap<>();
    private final ConcurrentMap<Double, InternedDouble> internedDoubles = new ConcurrentHashMap<>();

    private static final InternedBoolean INTERNED_TRUE = new InternedBoolean(true);
    private static final InternedBoolean INTERNED_FALSE = new InternedBoolean(false);

    @Override
    protected LuneContext createContext(Env env) {
        return new LuneContext();
    }

    public static LuneLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    @TruffleBoundary
    public InternedString internWithoutCopying(byte[] string) {
        return internedStrings.computeIfAbsent(new InternedStringKey(string), key -> new InternedString(key.bytes));
    }

    @TruffleBoundary
    public InternedString intern(byte[] string) {
        return internWithoutCopying(Arrays.copyOf(string, string.length));
    }

    @TruffleBoundary
    public InternedString intern(String javaString) {
        return internWithoutCopying(javaString.getBytes(StandardCharsets.UTF_8));
    }

    public InternedBoolean intern(boolean value) {
        if (value) {
            return INTERNED_TRUE;
        } else {
            return INTERNED_FALSE;
        }
    }

    @TruffleBoundary
    public InternedLong intern(long value) {
        return internedLongs.computeIfAbsent(value, InternedLong::new);
    }

    @TruffleBoundary
    public InternedDouble intern(double value) {
        return internedDoubles.computeIfAbsent(value, InternedDouble::new);
    }
}
