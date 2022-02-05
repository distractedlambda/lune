package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Closure;

public abstract class ClosureCallNode extends LuneNode {
    public abstract Object execute(Closure closure, Object[] arguments);

    @Specialization(guards = "closure.getCallTarget() == callNode.getCallTarget()", limit = "3")
    protected Object direct(
        Closure closure,
        Object[] arguments,
        @Cached("create(closure.getCallTarget())") DirectCallNode callNode
    ) {
        return callNode.call(arguments);
    }

    @Specialization(replaces = "direct")
    protected Object indirect(Closure closure, Object[] arguments, @Cached IndirectCallNode callNode) {
        return callNode.call(closure.getCallTarget(), arguments);
    }
}