package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.Shape;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Closure;

public abstract class ClosureCallTargetNode extends LuneNode {
    public abstract CallTarget execute(Closure closure);

    @Specialization(limit = "1", guards = "closure == cachedClosure")
    protected CallTarget constantClosure(
        Closure closure,
        @Cached(value = "closure", weak = true) Closure cachedClosure,
        @Cached(value = "closure.getCallTarget()", weak = true) CallTarget callTarget
    ) {
        return callTarget;
    }

    @Specialization(limit = "1", guards = "closure.getShape() == cachedShape", replaces = "constantClosure")
    protected CallTarget constantShape(
        Closure closure,
        @Cached(value = "closure.getShape()", weak = true) Shape cachedShape
    ) {
        return (CallTarget) cachedShape.getDynamicType();
    }

    @Specialization(replaces = "constantShape")
    protected CallTarget dynamic(Closure closure) {
        return closure.getCallTarget();
    }
}
