package org.lunelang.language.nodes.builtins;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.runtime.Builtin;

import static org.lunelang.language.Todo.TODO;

@GenerateUncached
@GenerateNodeFactory
@NodeInfo(shortName = "error")
public abstract class ErrorBuiltinNode extends Builtin.BodyNode {
    @Specialization
    protected Object impl(Object arguments) {
        throw TODO();
    }
}
