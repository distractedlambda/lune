package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.runtime.FloatingPoint;

@ReportPolymorphism
public abstract class FloorDivideNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return FloorDivideNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        longResult(frame, Math.floorDiv(lhs, rhs));
    }

    @Specialization
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        doubleDouble(frame, lhs, rhs);
    }

    @Specialization
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs) {
        doubleDouble(frame, lhs, rhs);
    }

    @Specialization
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        doubleResult(frame, FloatingPoint.floorDivide(lhs, rhs));
    }
}
