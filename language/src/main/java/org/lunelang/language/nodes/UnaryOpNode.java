package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;

import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;

@NodeField(name = "operandSlot", type = int.class)
public abstract class UnaryOpNode extends SingleResultInstructionNode {
    protected abstract int getOperandSlot();

    @Override
    public final void execute(VirtualFrame frame) {
        switch (frame.getTag(getOperandSlot())) {
            case FrameSlotTags.BOOLEAN -> execute(frame, frame.getBoolean(getOperandSlot()));
            case FrameSlotTags.LONG -> execute(frame, frame.getLong(getOperandSlot()));
            case FrameSlotTags.DOUBLE -> execute(frame, frame.getDouble(getOperandSlot()));
            case FrameSlotTags.OBJECT -> execute(frame, frame.getObject(getOperandSlot()));
            default -> throw shouldNotReachHere();
        }
    }

    protected abstract void execute(VirtualFrame frame, boolean operand);

    protected abstract void execute(VirtualFrame frame, long operand);

    protected abstract void execute(VirtualFrame frame, double operand);

    protected abstract void execute(VirtualFrame frame, Object operand);
}
