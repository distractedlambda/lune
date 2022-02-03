package org.lunelang.language.nodes.tables;

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
        @Cached("closure") Closure cachedClosure,
        @Cached("closure.getCallTarget()") CallTarget callTarget
    ) {
        return callTarget;
    }

    @Specialization(limit = "1", guards = "closure.getShape() == cachedShape", replaces = "constantClosure")
    protected CallTarget constantShape(
        Closure closure,
        @Cached("closure.getShape()") Shape cachedShape,
        @Cached("closure.getCallTarget()") CallTarget callTarget
    ) {
        return callTarget;
    }

    @Specialization(replaces = "constantShape")
    protected CallTarget dynamic(Closure closure) {
        return closure.getCallTarget();
    }
}
