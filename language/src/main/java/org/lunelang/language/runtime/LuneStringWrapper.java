package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.TriState;
import org.lunelang.language.LuneLanguage;

import java.nio.charset.StandardCharsets;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;

@ValueType
@ExportLibrary(InteropLibrary.class)
public final class LuneStringWrapper implements TruffleObject {
    @CompilationFinal(dimensions = 1) private final byte[] bytes;

    public LuneStringWrapper(byte[] bytes) {
        assert bytes != null;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @ExportMessage
    Class<LuneLanguage> getLanguage() {
        return LuneLanguage.class;
    }

    @ExportMessage
    boolean isString() {
        return true;
    }

    @ExportMessage
    protected static final class AsString {
        @Specialization(guards = "receiver.getBytes() == cachedBytes")
        static String cached(
            LuneStringWrapper receiver,
            @Cached(value = "receiver.getBytes()", weak = true, dimensions = 0) byte[] cachedBytes,
            @Cached("impl(cachedBytes)") String cachedResult
        ) {
            return cachedResult;
        }

        @Specialization(replaces = "cached")
        static String uncached(LuneStringWrapper receiver) {
            return impl(receiver.bytes);
        }

        @TruffleBoundary(allowInlining = true)
        static String impl(byte[] string) {
            return new String(string, StandardCharsets.UTF_8);
        }
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    int identityHashCode() {
        return System.identityHashCode(bytes);
    }

    @ExportMessage
    TriState isIdenticalOrUndefined(Object object) {
        if (!(object instanceof LuneStringWrapper other)) {
            return TriState.FALSE;
        }

        return TriState.valueOf(bytes == other.bytes);
    }

    @ExportMessage
    LuneStringWrapper toDisplayString(boolean allowSideEffects) {
        return this;
    }

    @Override
    public int hashCode() {
        neverPartOfCompilation();
        return System.identityHashCode(bytes);
    }

    @Override
    public boolean equals(Object object) {
        neverPartOfCompilation();

        if (!(object instanceof LuneStringWrapper other)) {
            return false;
        }

        return bytes == other.bytes;
    }

    @Override
    public String toString() {
        neverPartOfCompilation();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
