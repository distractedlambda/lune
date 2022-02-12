package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.InternedStringSet;

import static com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public abstract class LongToStringNode extends LuneNode {
    public abstract byte[] execute(long value);

    @Specialization(guards = "value == cachedValue")
    protected byte[] cached(
        long value,
        @Cached("value") long cachedValue,
        @Cached(value = "impl(cachedValue)", dimensions = 1) byte[] string
    ) {
        return string;
    }

    @Specialization(replaces = "cached")
    protected byte[] uncached(long value) {
        return impl(value);
    }

    protected byte[] impl(long value) {
        return impl(getLanguage().getInternedStrings(), value);
    }

    @TruffleBoundary
    private static byte[] impl(InternedStringSet internedStrings, long value) {
        return internedStrings.intern(Long.toString(value));
    }
}
