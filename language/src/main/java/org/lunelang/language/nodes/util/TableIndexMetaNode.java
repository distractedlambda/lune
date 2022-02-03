package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Table;

public abstract class TableIndexMetaNode extends LuneNode {
    public abstract Object execute(Table table, Object key, Object normalizedKey, Object metavalue);

    @Specialization
    protected Object metamethod(
        Table table,
        Object key,
        Object normalizedKey,
        Closure metamethod,
        @Cached ClosureCallNode closureCallNode,
        @Cached ScalarizeNode scalarizeNode
    ) {
        return scalarizeNode.execute(closureCallNode.execute(metamethod, new Object[]{metamethod, table, key}));
    }
}
