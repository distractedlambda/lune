package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.lunelang.language.nodes.LuneNode;

import static java.lang.System.arraycopy;

// FIXME handle scalars on rhs
public abstract class ArrayPrependNode extends LuneNode {
    public abstract Object[] execute(Object prefix, Object suffix);

    @ExplodeLoop
    @Specialization(limit = "1", guards = "suffix.length == cachedLength")
    protected Object[] staticLength(Object prefix, Object[] suffix, @Cached("suffix.length") int cachedLength) {
        var result = new Object[cachedLength];
        result[0] = prefix;
        for (var i = 0; i < cachedLength; i++) result[i + 1] = suffix[i];
        return result;
    }

    @Specialization(replaces = "staticLength")
    protected Object[] dynamicLength(Object prefix, Object[] suffix) {
        return dynamicLengthBoundary(prefix, suffix);
    }

    @TruffleBoundary(allowInlining = true)
    private static Object[] dynamicLengthBoundary(Object value, Object[] array) {
        var result = new Object[array.length + 1];
        result[0] = value;
        arraycopy(array, 0, result, 1, array.length);
        return result;
    }
}
