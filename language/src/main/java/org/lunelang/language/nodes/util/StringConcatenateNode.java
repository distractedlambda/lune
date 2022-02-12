package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.InternedStringSet;

import static java.lang.System.arraycopy;

public abstract class StringConcatenateNode extends LuneNode {
    public abstract byte[] execute(byte[] lhs, byte[] rhs);

    @Specialization(guards = {"lhs == cachedLhs", "rhs == cachedRhs"})
    protected byte[] cached(
        byte[] lhs,
        byte[] rhs,
        @Cached(value = "lhs", weak = true, dimensions = 0) byte[] cachedLhs,
        @Cached(value = "rhs", weak = true, dimensions = 0) byte[] cachedRhs,
        @Cached(value = "impl(cachedLhs, cachedRhs)", dimensions = 1) byte[] concatenated
    ) {
        return concatenated;
    }

    @Specialization(replaces = "cached")
    protected byte[] uncached(byte[] lhs, byte[] rhs) {
        return impl(lhs, rhs);
    }

    protected byte[] impl(byte[] lhs, byte[] rhs) {
        return impl(getLanguage().getInternedStrings(), lhs, rhs);
    }

    @TruffleBoundary
    private static byte[] impl(InternedStringSet internedStrings, byte[] lhs, byte[] rhs) {
        var combined = new byte[lhs.length + rhs.length];
        arraycopy(lhs, 0, combined, 0, lhs.length);
        arraycopy(rhs, 0, combined, lhs.length, rhs.length);
        return internedStrings.intern(combined);
    }
}
