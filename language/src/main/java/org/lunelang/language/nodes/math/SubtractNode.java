package org.lunelang.language.nodes.math;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;

public abstract class SubtractNode extends BinaryOpNode {
    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        longResult(frame, lhs - rhs);
    }

    @Specialization
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        doubleResult(frame, lhs - rhs);
    }

    @Specialization
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs) {
        doubleResult(frame, lhs - rhs);
    }

    @Specialization
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        doubleResult(frame, lhs - rhs);
    }
}
