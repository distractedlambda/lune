package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.CaptureBox;

public abstract class CaptureBoxLoadNode extends LuneNode {
    public abstract void execute(VirtualFrame frame, int resultSlot, CaptureBox captureBox);

    @Specialization(guards = "captureBox.isBoolean()")
    protected void booleanCapture(VirtualFrame frame, int resultSlot, CaptureBox captureBox) {
        frame.setBoolean(resultSlot, captureBox.getBoolean());
    }

    @Specialization(guards = "captureBox.isLong()")
    protected void longCapture(VirtualFrame frame, int resultSlot, CaptureBox captureBox) {
        frame.setLong(resultSlot, captureBox.getLong());
    }

    @Specialization(guards = "captureBox.isDouble()")
    protected void doubleCapture(VirtualFrame frame, int resultSlot, CaptureBox captureBox) {
        frame.setDouble(resultSlot, captureBox.getDouble());
    }

    @Specialization(guards = "captureBox.isObject()")
    protected void objectCapture(VirtualFrame frame, int resultSlot, CaptureBox captureBox) {
        frame.setObject(resultSlot, captureBox.getObject());
    }
}
