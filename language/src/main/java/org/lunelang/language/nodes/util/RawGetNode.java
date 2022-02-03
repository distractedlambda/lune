package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

public abstract class RawGetNode extends LuneNode {
    public abstract Object execute(Table table, Object normalizedKey);

    @Specialization(guards = {"normalizedKey >= 1", "normalizedKey <= table.getSequenceLength(tables)"})
    protected Object getSequenceElement(
        Table table,
        long normalizedKey,
        @CachedLibrary(limit = "3") DynamicObjectLibrary tables
    ) {
        return table.getSequenceStorage(tables)[(int) (normalizedKey - 1)];
    }

    @Fallback
    protected Object getProperty(
        Table table,
        Object normalizedKey,
        @CachedLibrary(limit = "3") DynamicObjectLibrary tables
    ) {
        return tables.getOrDefault(table, normalizedKey, Nil.getInstance());
    }
}
