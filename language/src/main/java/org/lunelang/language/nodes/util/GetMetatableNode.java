package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.Shape;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Builtin;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

@GenerateUncached
public abstract class GetMetatableNode extends LuneNode {
    public abstract Object execute(Object subject);

    @Specialization
    protected Object nilMetatable(Nil subject) {
        return getContext().getNilMetatable();
    }

    @Specialization
    protected Object booleanMetatable(boolean subject) {
        return getContext().getBooleanMetatable();
    }

    @Specialization
    protected Object longMetatable(long subject) {
        return getContext().getNumberMetatable();
    }

    @Specialization
    protected Object doubleMetatable(double subject) {
        return getContext().getNumberMetatable();
    }

    @Specialization
    protected Object stringMetatable(byte[] subject) {
        return getContext().getStringMetatable();
    }

    @Specialization
    protected Object builtinMetatable(Builtin subject) {
        return getContext().getFunctionMetatable();
    }

    @Specialization
    protected Object closureMetatable(Closure subject) {
        return getContext().getFunctionMetatable();
    }

    @Specialization(limit = "1", guards = "subject.getShape() == cachedShape")
    protected Object cachedTableMetatable(
        Table subject,
        @Cached(value = "subject.getShape()", weak = true) Shape cachedShape
    ) {
        return cachedShape.getDynamicType();
    }

    @Specialization(replaces = "cachedTableMetatable")
    protected Object uncachedTableMetatable(Table subject) {
        return subject.getShape().getDynamicType();
    }
}
