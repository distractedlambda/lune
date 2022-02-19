package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Table;

import static org.lunelang.language.Todo.TODO;

public abstract class RawGetNode extends LuneNode {
    public abstract Object execute(Table table, Object normalizedKey);

    @Specialization(guards = {"normalizedKey >= 1", "normalizedKey <= table.getArraySize()"})
    protected Object getSequenceElement(Table table, long normalizedKey) {
        throw TODO();
    }

    @Fallback
    protected Object getProperty(Table table, Object normalizedKey) {
        throw TODO();
    }
}
