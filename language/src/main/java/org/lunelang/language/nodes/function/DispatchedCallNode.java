package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import org.lunelang.language.nodes.LuneNode;

public abstract class DispatchedCallNode extends LuneNode {
    public abstract Object execute(CallTarget callTarget, Object[] arguments);

    @Specialization(guards = "callTarget == callNode.getCallTarget()")
    protected Object direct(
        CallTarget callTarget,
        Object[] arguments,
        @Cached("create(callTarget)") DirectCallNode callNode
    ) {
        return callNode.call(arguments);
    }

    @Specialization(replaces = "direct")
    protected Object indirect(CallTarget callTarget, Object[] arguments, @Cached IndirectCallNode callNode) {
        return callNode.call(callTarget, arguments);
    }
}
