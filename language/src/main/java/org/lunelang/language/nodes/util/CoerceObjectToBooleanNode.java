package org.lunelang.language.nodes.util;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.Nil;

@GenerateUncached
public abstract class CoerceObjectToBooleanNode extends LuneNode {
    public abstract boolean execute(Object value);

    @Specialization
    protected boolean coerceNil(Nil operand) {
        return false;
    }

    @Specialization
    protected boolean coerceBoolean(boolean operand) {
        return operand;
    }

    @Fallback
    protected boolean coerceOther(Object operand) {
        return true;
    }
}
