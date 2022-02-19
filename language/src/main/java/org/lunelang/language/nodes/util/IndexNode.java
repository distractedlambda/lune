package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.nodes.table.NormalizeTableKeyNode;
import org.lunelang.language.runtime.Table;

import static org.lunelang.language.Todo.TODO;

public abstract class IndexNode extends LuneNode {
    public abstract Object execute(Object receiver, Object key);

    @Specialization
    protected Object indexTable(
        Table receiver,
        Object key,
        @Cached NormalizeTableKeyNode normalizeTableKeyNode
    ) {
        throw TODO();
        // var normalizedKey = normalizeTableKeyNode.execute(key);
        // var value = tables.getOrDefault(receiver, normalizedKey, Nil.getInstance());

        // if (!isNil(value)) {
        //     return value;
        // }

        // if (!(tables.getDynamicType(receiver) instanceof Table metatable)) {
        //     return Nil.getInstance();
        // }

        // var indexMetavalue = metatables.getOrDefault(metatable, getLanguage().getIndexMetavalueKey(), Nil.getInstance());

        // if (isNil(indexMetavalue)) {
        //     return Nil.getInstance();
        // }

        // throw new UnsupportedOperationException("TODO");
    }
}
