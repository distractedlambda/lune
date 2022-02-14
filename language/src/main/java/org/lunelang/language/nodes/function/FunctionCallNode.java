package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.nodes.util.ArrayPrependNode;
import org.lunelang.language.nodes.util.GetMetatableNode;
import org.lunelang.language.runtime.Closure;

import static org.lunelang.language.Todo.TODO;

public abstract class FunctionCallNode extends LuneNode {
    public abstract Object execute(Object callee, Object[] arguments);

    @Specialization
    protected Object closure(
        Closure callee,
        Object[] arguments,
        @Cached ArrayPrependNode calleePrependNode,
        @Cached ClosureCallTargetNode closureCallTargetNode,
        @Cached DispatchedCallNode callNode
    ) {
        return callNode.execute(closureCallTargetNode.execute(callee), calleePrependNode.execute(callee, arguments));
    }

    @Fallback
    protected Object throughMetamethod(
        Object callee,
        Object[] arguments,
        @Cached GetMetatableNode getMetatableNode,
        @Cached FunctionCallThroughMetatableNode callThroughMetatableNode
    ) {
        var result = callThroughMetatableNode.execute(callee, getMetatableNode.execute(callee), arguments);

        if (result == null) {
            throw TODO();
        }

        return result;
    }
}
