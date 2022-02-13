package org.lunelang.language.nodes.function;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

public abstract class CallThroughMetatableNode extends LuneNode {
    public abstract Object execute(Object callee, Object metatable, Object[] arguments);

    @Specialization
    protected Object presentMetatable(
        Object callee,
        Table metatable,
        Object[] arguments,
        @CachedLibrary(limit = "3") DynamicObjectLibrary dynamicObjects,
        @Cached CallThroughMetamethodNode callThroughMetamethodNode
    ) {
        var callMetamethod = dynamicObjects.getOrDefault(
            metatable,
            getLanguage().getCallMetamethodKey(),
            Nil.getInstance()
        );

        return callThroughMetamethodNode.execute(callee, callMetamethod, arguments);
    }

    @Fallback
    protected Object missingMetatable(Object callee, Object metatable, Object[] arguments) {
        assert isNil(metatable);
        return null;
    }
}
