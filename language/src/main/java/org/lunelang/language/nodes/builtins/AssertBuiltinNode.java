package org.lunelang.language.nodes.builtins;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.nodes.util.CoerceObjectToBooleanNode;
import org.lunelang.language.nodes.util.ScalarizeNode;
import org.lunelang.language.runtime.Builtin;

import static com.oracle.truffle.api.CompilerDirectives.transferToInterpreter;
import static org.lunelang.language.Todo.TODO;

@GenerateUncached
@GenerateNodeFactory
@NodeInfo(shortName = "assert")
public abstract class AssertBuiltinNode extends Builtin.BodyNode {
    @Specialization
    protected Object impl(
        Object arguments,
        @Cached ScalarizeNode scalarizeNode,
        @Cached CoerceObjectToBooleanNode coerceObjectToBooleanNode
    ) {
        if (coerceObjectToBooleanNode.execute(scalarizeNode.execute(arguments))) {
            return arguments;
        }

        transferToInterpreter();
        throw TODO();
    }
}
