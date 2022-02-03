package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;

public abstract class ScalarizeNode extends LuneNode {
    public abstract Object execute(Object values);

    @Specialization(guards = "values.length == 0")
    protected Nil scalarizeEmptyArray(Object[] values) {
        return Nil.getInstance();
    }

    @Specialization(guards = "values.length != 0")
    protected Object scalarizeNonEmptyArray(Object[] values) {
        return values[0];
    }

    @Specialization(guards = "isNotArray(values)")
    protected Object scalarizeScalar(Object values) {
        return values;
    }

    protected static boolean isNotArray(Object values) {
        return values.getClass() != Object[].class;
    }
}
