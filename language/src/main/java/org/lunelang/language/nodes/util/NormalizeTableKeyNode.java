package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.FloatingPoint;
import org.lunelang.language.runtime.Nil;

@ImportStatic(FloatingPoint.class)
public abstract class NormalizeTableKeyNode extends LuneNode {
    public abstract Object execute(Object key);

    @Specialization
    protected Void normalizeNil(Nil value) {
        throw new UnsupportedOperationException("TODO throw proper error here");
    }

    @Specialization(guards = "isNaN(value)")
    protected Void normalizeNaN(double value) {
        throw new UnsupportedOperationException("TODO throw proper error here");
    }

    @Specialization(guards = "hasExactLongValue(value)")
    protected long normalizeIntegralDouble(double value) {
        return (long) value;
    }

    @Fallback
    protected Object normalizeOther(Object value) {
        return value;
    }
}
