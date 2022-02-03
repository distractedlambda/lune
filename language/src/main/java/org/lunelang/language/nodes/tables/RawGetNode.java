package org.lunelang.language.nodes.tables;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Table;

public abstract class RawGetNode extends LuneNode {
    public abstract Object execute(Table table, Object key);

    @Specialization
    protected Object impl(
        Table table,
        Object key,
        @Cached NormalizeTableKeyNode normalizeTableKeyNode,
        @CachedLibrary(limit = "3") DynamicObjectLibrary tables
    ) {
        return tables.getOrDefault(table, normalizeTableKeyNode.execute(key), null);
    }
}
