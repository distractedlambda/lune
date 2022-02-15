package org.lunelang.language.nodes.builtins;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.Todo;
import org.lunelang.language.nodes.LuneNode;

import static com.oracle.truffle.api.CompilerDirectives.transferToInterpreter;

@GenerateUncached
abstract class ExtractFirstArgumentNode extends LuneNode {
    abstract Object execute(Object arguments);

    @Specialization
    protected Object vectorArguments(Object[] arguments) {
        if (arguments.length == 0) {
            transferToInterpreter();
            throw Todo.TODO();
        }

        return arguments[0];
    }

    @Fallback
    protected Object scalarArgument(Object arguments) {
        return arguments;
    }
}
