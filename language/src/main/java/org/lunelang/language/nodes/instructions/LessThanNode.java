package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;

public abstract class LessThanNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return null;
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs) {
        booleanResult(frame, lhs < rhs);
    }

    @Specialization
    protected void longDouble(VirtualFrame frame, long lhs, double rhs) {
        booleanResult(frame, longDoubleBoundary(lhs, rhs));
    }

    @TruffleBoundary(allowInlining = true)
    private static boolean longDoubleBoundary(long lhs, double rhs) {
        if (Double.isInfinite(rhs)) {
            return rhs > 0;
        }

        if (Double.isNaN(rhs)) {
            return false;
        }

        // TODO
    }

    @Specialization
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs) {
        booleanResult(frame, lhs < rhs);
    }
}
