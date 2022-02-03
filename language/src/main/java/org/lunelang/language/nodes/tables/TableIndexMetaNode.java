package org.lunelang.language.nodes.tables;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Table;

public abstract class TableIndexMetaNode extends LuneNode {
    public abstract Object execute(Table table, Object key, Object normalizedKey, Object indexMetavalue);

    @Specialization
    protected Object metamethod(
        Table table,
        Object key,
        Object normalizedKey,
        Closure indexMetamethod,
        @Cached ClosureCallNode closureCallNode,
        @Cached ScalarizeNode scalarizeNode
    ) {
        return scalarizeNode.execute(closureCallNode.execute(indexMetamethod, new Object[]{indexMetamethod, table, key}));
    }


}
