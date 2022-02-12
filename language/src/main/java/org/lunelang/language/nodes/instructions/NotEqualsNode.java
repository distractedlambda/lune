package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;

import static org.lunelang.language.runtime.FloatingPoint.hasExactLongValue;

public abstract class NotEqualsNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return NotEqualsNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void booleanBoolean(VirtualFrame frame, boolean lhs, boolean rhs) {
        booleanResult(frame, lhs != rhs);
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        booleanResult(frame, lhs != rhs);
    }

    @Specialization
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        booleanResult(frame, !hasExactLongValue(rhs) || lhs != (long) rhs);
    }

    @Specialization
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs) {
        booleanResult(frame, !hasExactLongValue(lhs) || (long) lhs != rhs);
    }

    @Specialization
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        booleanResult(frame, lhs != rhs);
    }

    @Fallback
    protected void objects(VirtualFrame frame, Object lhs, Object rhs) {
        booleanResult(frame, lhs != rhs);
    }
}
