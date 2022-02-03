package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

public abstract class IndexNode extends LuneNode {
    public abstract Object execute(Object receiver, Object key);

    @Specialization
    protected Object indexTable(
        Table receiver,
        Object key,
        @Cached NormalizeTableKeyNode normalizeTableKeyNode,
        @CachedLibrary(limit = "3") DynamicObjectLibrary tables,
        @CachedLibrary(limit = "3") DynamicObjectLibrary metatables
    ) {
        var normalizedKey = normalizeTableKeyNode.execute(key);
        var value = tables.getOrDefault(receiver, normalizedKey, Nil.getInstance());

        if (!isNil(value)) {
            return value;
        }

        if (!(tables.getDynamicType(receiver) instanceof Table metatable)) {
            return Nil.getInstance();
        }

        var indexMetavalue = metatables.getOrDefault(metatable, getContext().getIndexMetavalueKey(), Nil.getInstance());

        if (isNil(indexMetavalue)) {
            return Nil.getInstance();
        }

        throw new UnsupportedOperationException("TODO");
    }
}
