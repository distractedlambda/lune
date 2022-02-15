package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;

@GenerateUncached
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

    @Fallback
    protected Object scalarizeScalar(Object values) {
        return values;
    }
}
