package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.nodes.util.ArrayPrependNode;
import org.lunelang.language.runtime.Nil;

public abstract class CallThroughMetamethodNode extends LuneNode {
    public abstract Object execute(Object callee, Object callMetamethod, Object[] arguments);

    @Specialization
    protected Object missingMetamethod(Object callee, Nil callMetamethod, Object[] arguments) {
        return null;
    }

    @Fallback
    protected Object presentMetamethod(
        Object callee,
        Object callMetamethod,
        Object[] arguments,
        @Cached ArrayPrependNode calleePrependNode,
        @Cached CallNode metamethodCallNode
    ) {
        return metamethodCallNode.execute(callMetamethod, calleePrependNode.execute(callee, arguments));
    }
}
