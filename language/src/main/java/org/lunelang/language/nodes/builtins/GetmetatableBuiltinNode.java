package org.lunelang.language.nodes.builtins;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.util.GetMetatableNode;
import org.lunelang.language.runtime.Builtin;
import org.lunelang.language.runtime.Nil;
import org.lunelang.language.runtime.Table;

import static org.lunelang.language.Todo.TODO;

@GenerateUncached
@GenerateNodeFactory
public abstract class GetmetatableBuiltinNode extends Builtin.BodyNode {
    @Specialization
    protected Object impl(
        Object arguments,
        @Cached ExtractFirstArgumentNode extractFirstArgumentNode,
        @Cached GetMetatableNode getMetatableNode
    ) {
        if (!(getMetatableNode.execute(extractFirstArgumentNode.execute(arguments)) instanceof Table trueMetatable)) {
            return Nil.getInstance();
        }

        throw TODO();

        // var substitutedMetatable = dynamicObjects.getOrDefault(
        //     trueMetatable,
        //     getLanguage().getMetatableMetavalueKey(),
        //     Nil.getInstance()
        // );

        // return isNil(substitutedMetatable) ? trueMetatable : substitutedMetatable;
    }
}
