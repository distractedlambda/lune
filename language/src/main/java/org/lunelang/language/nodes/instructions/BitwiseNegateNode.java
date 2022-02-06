package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;
import org.lunelang.language.runtime.FloatingPoint;

@ReportPolymorphism
@ImportStatic(FloatingPoint.class)
public abstract class BitwiseNegateNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return BitwiseNegateNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void onLong(VirtualFrame frame, long operand) {
        longResult(frame, ~operand);
    }

    @Specialization(guards = "hasExactLongValue(operand)")
    protected void onIntegralDouble(VirtualFrame frame, double operand) {
        onLong(frame, (long) operand);
    }
}
