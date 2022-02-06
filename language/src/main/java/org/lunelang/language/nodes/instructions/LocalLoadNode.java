package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;
import org.lunelang.language.runtime.CaptureBox;

public abstract class LocalLoadNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return LocalLoadNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void capture(VirtualFrame frame, CaptureBox captureBox, @Cached CaptureBoxLoadNode captureBoxLoadNode) {
        captureBoxLoadNode.execute(frame, getResultSlot(), captureBox);
    }

    @Fallback
    protected void object(VirtualFrame frame, Object value) {
        genericResult(frame, value);
    }

    @Override
    protected final void execute(VirtualFrame frame, boolean operand) {
        booleanResult(frame, operand);
    }

    @Override
    protected final void execute(VirtualFrame frame, long operand) {
        longResult(frame, operand);
    }

    @Override
    protected final void execute(VirtualFrame frame, double operand) {
        doubleResult(frame, operand);
    }
}
