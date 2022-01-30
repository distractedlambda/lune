package org.lunelang.language.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class SingleResultInstructionNode extends InstructionNode {
    private final int resultSlot;

    protected SingleResultInstructionNode(int resultSlot) {
        this.resultSlot = resultSlot;
    }

    protected final int getResultSlot() {
        return resultSlot;
    }

    protected final void booleanResult(VirtualFrame frame, boolean value) {
        frame.setBoolean(getResultSlot(), value);
    }

    protected final void longResult(VirtualFrame frame, long value) {
        frame.setLong(getResultSlot(), value);
    }

    protected final void doubleResult(VirtualFrame frame, double value) {
        frame.setDouble(getResultSlot(), value);
    }

    protected final void genericResult(VirtualFrame frame, Object value) {
        frame.setObject(getResultSlot(), value);
    }
}
