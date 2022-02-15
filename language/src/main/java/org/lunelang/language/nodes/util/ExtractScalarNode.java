package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;

@NodeField(name = "index", type = int.class)
public abstract class ExtractScalarNode extends LuneNode {
    public abstract Object execute(Object values);

    protected abstract int getIndex();

    @Specialization(guards = "index < vector.length")
    protected Object vectorInBounds(Object[] vector) {
        return vector[getIndex()];
    }

    @Specialization(guards = "index >= vector.length")
    protected Object vectorOutOfBounds(Object[] vector) {
        return Nil.getInstance();
    }

    @Fallback
    protected Object scalar(Object scalar) {
        return getIndex() == 0 ? scalar : Nil.getInstance();
    }
}
