package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;

import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;

@NodeField(name = "lhsSlot", type = int.class)
@NodeField(name = "rhsSlot", type = int.class)
public abstract class BinaryOpNode extends SingleResultInstructionNode {
    protected abstract int getLhsSlot();

    protected abstract int getRhsSlot();

    @Override
    public final void execute(VirtualFrame frame) {
        switch (frame.getTag(getLhsSlot())) {
            case FrameSlotTags.BOOLEAN -> {
                switch (frame.getTag(getRhsSlot())) {
                    case FrameSlotTags.BOOLEAN -> execute(frame, frame.getBoolean(getLhsSlot()), frame.getBoolean(getRhsSlot()));
                    case FrameSlotTags.LONG -> execute(frame, frame.getBoolean(getLhsSlot()), frame.getLong(getRhsSlot()));
                    case FrameSlotTags.DOUBLE -> execute(frame, frame.getBoolean(getLhsSlot()), frame.getDouble(getRhsSlot()));
                    case FrameSlotTags.OBJECT -> execute(frame, frame.getBoolean(getLhsSlot()), frame.getObject(getRhsSlot()));
                    default -> throw shouldNotReachHere();
                }
            }

            case FrameSlotTags.LONG -> {
                switch (frame.getTag(getRhsSlot())) {
                    case FrameSlotTags.BOOLEAN -> execute(frame, frame.getLong(getLhsSlot()), frame.getBoolean(getRhsSlot()));
                    case FrameSlotTags.LONG -> execute(frame, frame.getLong(getLhsSlot()), frame.getLong(getRhsSlot()));
                    case FrameSlotTags.DOUBLE -> execute(frame, frame.getLong(getLhsSlot()), frame.getDouble(getRhsSlot()));
                    case FrameSlotTags.OBJECT -> execute(frame, frame.getLong(getLhsSlot()), frame.getObject(getRhsSlot()));
                    default -> throw shouldNotReachHere();
                }
            }

            case FrameSlotTags.DOUBLE -> {
                switch (frame.getTag(getRhsSlot())) {
                    case FrameSlotTags.BOOLEAN -> execute(frame, frame.getDouble(getLhsSlot()), frame.getBoolean(getRhsSlot()));
                    case FrameSlotTags.LONG -> execute(frame, frame.getDouble(getLhsSlot()), frame.getLong(getRhsSlot()));
                    case FrameSlotTags.DOUBLE -> execute(frame, frame.getDouble(getLhsSlot()), frame.getDouble(getRhsSlot()));
                    case FrameSlotTags.OBJECT -> execute(frame, frame.getDouble(getLhsSlot()), frame.getObject(getRhsSlot()));
                    default -> throw shouldNotReachHere();
                }
            }

            case FrameSlotTags.OBJECT -> {
                switch (frame.getTag(getRhsSlot())) {
                    case FrameSlotTags.BOOLEAN -> execute(frame, frame.getObject(getLhsSlot()), frame.getBoolean(getRhsSlot()));
                    case FrameSlotTags.LONG -> execute(frame, frame.getObject(getLhsSlot()), frame.getLong(getRhsSlot()));
                    case FrameSlotTags.DOUBLE -> execute(frame, frame.getObject(getLhsSlot()), frame.getDouble(getRhsSlot()));
                    case FrameSlotTags.OBJECT -> execute(frame, frame.getObject(getLhsSlot()), frame.getObject(getRhsSlot()));
                    default -> throw shouldNotReachHere();
                }
            }

            default -> throw shouldNotReachHere();
        }
    }

    public abstract void execute(VirtualFrame frame, boolean lhs, boolean rhs);

    public abstract void execute(VirtualFrame frame, boolean lhs, long rhs);

    public abstract void execute(VirtualFrame frame, boolean lhs, double rhs);

    public abstract void execute(VirtualFrame frame, boolean lhs, Object rhs);

    public abstract void execute(VirtualFrame frame, long lhs, boolean rhs);

    public abstract void execute(VirtualFrame frame, long lhs, long rhs);

    public abstract void execute(VirtualFrame frame, long lhs, double rhs);

    public abstract void execute(VirtualFrame frame, long lhs, Object rhs);

    public abstract void execute(VirtualFrame frame, double lhs, boolean rhs);

    public abstract void execute(VirtualFrame frame, double lhs, long rhs);

    public abstract void execute(VirtualFrame frame, double lhs, double rhs);

    public abstract void execute(VirtualFrame frame, double lhs, Object rhs);

    public abstract void execute(VirtualFrame frame, Object lhs, boolean rhs);

    public abstract void execute(VirtualFrame frame, Object lhs, long rhs);

    public abstract void execute(VirtualFrame frame, Object lhs, double rhs);

    public abstract void execute(VirtualFrame frame, Object lhs, Object rhs);
}
