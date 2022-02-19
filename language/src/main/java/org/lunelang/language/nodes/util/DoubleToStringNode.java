package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.FloatingPoint;
import org.lunelang.language.runtime.InternedSet;

import java.nio.charset.StandardCharsets;

import static com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

@ImportStatic(FloatingPoint.class)
public abstract class DoubleToStringNode extends LuneNode {
    public abstract byte[] execute(double value);

    @Specialization(guards = "bitwiseEqual(value, cachedValue)")
    protected byte[] cached(
        double value,
        @Cached("value") double cachedValue,
        @Cached(value = "impl(cachedValue)", dimensions = 1) byte[] string
    ) {
        return string;
    }

    @Specialization(replaces = "cached")
    protected byte[] uncached(double value) {
        return impl(value);
    }

    protected byte[] impl(double value) {
        return impl(getLanguage().getInternedStrings(), value);
    }

    @TruffleBoundary
    private static byte[] impl(InternedSet<byte[]> internedStrings, double value) {
        return internedStrings.intern(Double.toString(value).getBytes(StandardCharsets.UTF_8));
    }
}
