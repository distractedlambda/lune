package org.lunelang.language.nodes.misc;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

public abstract class GetMetatableNode extends LuneNode {
    public abstract Object execute(Object subject);

    @Specialization
    protected Object ofNil(Nil subject) {
        return getContext().getNilMetatable();
    }

    @Specialization
    protected Object ofBoolean(boolean subject) {
        return getContext().getBooleanMetatable();
    }

    @Specialization
    protected Object ofLong(long subject) {
        return getContext().getNumberMetatable();
    }

    @Specialization
    protected Object ofDouble(double subject) {
        return getContext().getNumberMetatable();
    }

    @Specialization
    protected Object ofString(byte[] subject) {
        return getContext().getStringMetatable();
    }

    @Specialization(limit = "3")
    protected Object ofTable(Table subject, @CachedLibrary("subject") DynamicObjectLibrary subjects) {
        return subjects.getOrDefault(subject, Table.METATABLE, Nil.getInstance());
    }
}
