package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.nodes.util.ArrayPrependNode;
import org.lunelang.language.runtime.Nil;

public abstract class FunctionCallThroughMetamethodNode extends LuneNode {
    public abstract Object execute(Object callee, Object callMetamethod, Object[] arguments);

    @Specialization
    protected Object metamethodAbsent(Object callee, Nil callMetamethod, Object[] arguments) {
        return null;
    }

    @Fallback
    protected Object metamethodPresent(
        Object callee,
        Object callMetamethod,
        Object[] arguments,
        @Cached ArrayPrependNode calleePrependNode,
        @Cached FunctionCallNode metamethodCallNode
    ) {
        return metamethodCallNode.execute(callMetamethod, calleePrependNode.execute(callee, arguments));
    }
}
