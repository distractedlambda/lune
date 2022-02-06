package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "resultSlot", type = int.class)
public abstract class SingleResultInstructionNode extends InstructionNode {
    protected abstract int getResultSlot();

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
