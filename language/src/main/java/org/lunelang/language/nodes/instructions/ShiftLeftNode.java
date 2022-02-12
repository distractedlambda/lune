package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.LuneNode;
import org.lunelang.language.runtime.FloatingPoint;

@NodeInfo(shortName = "shl")
@ImportStatic(FloatingPoint.class)
public abstract class ShiftLeftNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return ShiftLeftNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void longLong(VirtualFrame frame, long lhs, long rhs, @Shared("implNode") @Cached ImplNode implNode) {
        longResult(frame, implNode.execute(lhs, rhs));
    }

    @Specialization(guards = "hasExactLongValue(rhs)")
    protected void longDouble(VirtualFrame frame, long lhs, double rhs, @Shared("implNode") @Cached ImplNode implNode) {
        longLong(frame, lhs, (long) rhs, implNode);
    }

    @Specialization(guards = "hasExactLongValue(lhs)")
    protected void doubleLong(VirtualFrame frame, double lhs, long rhs, @Shared("implNode") @Cached ImplNode implNode) {
        longLong(frame, (long) lhs, rhs, implNode);
    }

    @Specialization(guards = {"hasExactLongValue(lhs)", "hasExactLongValue(rhs)"})
    protected void doubleDouble(VirtualFrame frame, double lhs, double rhs, @Shared("implNode") @Cached ImplNode implNode) {
        longLong(frame, (long) lhs, (long) rhs, implNode);
    }

    protected static abstract class ImplNode extends LuneNode {
        protected abstract long execute(long lhs, long rhs);

        @Specialization(guards = "rhs > MAX_SHIFT")
        protected long overlyPositive(long lhs, long rhs) {
            return 0;
        }

        @Specialization(guards = "rhs < MIN_SHIFT")
        protected long overlyNegative(long lhs, long rhs) {
            return 0;
        }

        @Specialization(guards = {"rhs >= 0", "rhs <= MAX_SHIFT"})
        protected long positive(long lhs, long rhs) {
            return lhs << rhs;
        }

        @Specialization(guards = {"rhs < 0", "rhs >= MIN_SHIFT"})
        protected long negative(long lhs, long rhs) {
            return lhs >>> -rhs;
        }

        protected static final long MAX_SHIFT = 63;
        protected static final long MIN_SHIFT = -63;
    }
}
