package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Closure;
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

    @Specialization
    protected Object ofClosure(Closure subject) {
        return getContext().getFunctionMetatable();
    }

    @Specialization
    protected Object ofTable(Table subject) {
        return subject.getMetatable();
    }
}
