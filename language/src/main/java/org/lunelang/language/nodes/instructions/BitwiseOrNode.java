package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.runtime.FloatingPoint;

@ReportPolymorphism
@ImportStatic(FloatingPoint.class)
public abstract class BitwiseOrNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return BitwiseOrNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        longResult(frame, lhs | rhs);
    }

    @Specialization(guards = "hasExactLongValue(rhs)")
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        longLong(frame, lhs, (long) rhs);
    }

    @Specialization(guards = "hasExactLongValue(lhs)")
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs) {
        longLong(frame, (long) lhs, rhs);
    }

    @Specialization(guards = {"hasExactLongValue(lhs)", "hasExactLongValue(rhs)"})
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        longLong(frame, (long) lhs, (long) rhs);
    }
}
