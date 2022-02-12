package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;

public abstract class NegateNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return NegateNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void onLong(VirtualFrame frame, long operand) {
        longResult(frame, -operand);
    }

    @Specialization
    protected void onDouble(VirtualFrame frame, double operand) {
        doubleResult(frame, -operand);
    }
}
