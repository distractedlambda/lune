package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;

public abstract class CallNode extends LuneNode {
    public abstract Object execute(Object callee, Object[] arguments);

    @Specialization
    protected Object callNil(
        Nil callee,
        Object[] arguments,
        @Cached CallThroughMetatableNode callThroughMetatableNode
    ) {
        return callThroughMetatableNode.execute(callee, getContext().getNilMetatable(), arguments);
    }

    @Specialization
    protected Object callBoolean(
        Boolean callee,
        Object[] arguments,
        @Cached CallThroughMetatableNode callThroughMetatableNode
    ) {
        return callThroughMetatableNode.execute(callee, getContext().getBooleanMetatable(), arguments);
    }

    @Specialization
    protected Object callLong(
        long callee,
        Object[] arguments,
        @Cached CallThroughMetatableNode callThroughMetatableNode
    ) {
        return callThroughMetatableNode.execute(callee, getContext().getNumberMetatable(), arguments);
    }

    @Specialization
    protected Object callDouble(
        double callee,
        Object[] arguments,
         @Cached CallThroughMetatableNode callThroughMetatableNode
    ) {
        return callThroughMetatableNode.execute(callee, getContext().getNumberMetatable(), arguments);
    }
}
