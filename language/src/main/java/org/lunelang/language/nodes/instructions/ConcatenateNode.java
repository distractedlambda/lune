package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.nodes.BinaryOpNode;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.util.DoubleToStringNode;
import org.lunelang.language.nodes.util.LongToStringNode;
import org.lunelang.language.nodes.util.StringConcatenateNode;
import org.lunelang.language.runtime.FloatingPoint;

import static com.oracle.truffle.api.dsl.Cached.Shared;

@NodeInfo(shortName = "concat")
@ImportStatic(FloatingPoint.class)
public abstract class ConcatenateNode extends BinaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return ConcatenateNodeGen.create(getResultSlot(), getLhsSlot(), getRhsSlot());
    }

    @Specialization
    protected void longLong(
        VirtualFrame frame,
        long lhs,
        long rhs,
        @Shared("lhsLongToStringNode") @Cached LongToStringNode lhsLongToStringNode,
        @Shared("rhsLongToStringNode") @Cached LongToStringNode rhsLongToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsLongToStringNode.execute(lhs), rhsLongToStringNode.execute(rhs)));
    }

    @Specialization
    protected void longDouble(
        VirtualFrame frame,
        long lhs,
        double rhs,
        @Shared("lhsLongToStringNode") @Cached LongToStringNode lhsLongToStringNode,
        @Shared("rhsDoubleToStringNode") @Cached DoubleToStringNode rhsDoubleToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsLongToStringNode.execute(lhs), rhsDoubleToStringNode.execute(rhs)));
    }

    @Specialization
    protected void longString(
        VirtualFrame frame,
        long lhs,
        byte[] rhs,
        @Shared("lhsLongToStringNode") @Cached LongToStringNode lhsLongToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsLongToStringNode.execute(lhs), rhs));
    }

    @Specialization
    protected void doubleLong(
        VirtualFrame frame,
        double lhs,
        long rhs,
        @Shared("lhsDoubleToStringNode") @Cached DoubleToStringNode lhsDoubleToStringNode,
        @Shared("rhsLongToStringNode") @Cached LongToStringNode rhsLongToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsDoubleToStringNode.execute(lhs), rhsLongToStringNode.execute(rhs)));
    }

    @Specialization
    protected void doubleDouble(
        VirtualFrame frame,
        double lhs,
        double rhs,
        @Shared("lhsDoubleToStringNode") @Cached DoubleToStringNode lhsDoubleToStringNode,
        @Shared("rhsDoubleToStringNode") @Cached DoubleToStringNode rhsDoubleToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsDoubleToStringNode.execute(lhs), rhsDoubleToStringNode.execute(rhs)));
    }

    @Specialization
    protected void doubleString(
        VirtualFrame frame,
        double lhs,
        byte[] rhs,
        @Shared("lhsDoubleToStringNode") @Cached DoubleToStringNode lhsDoubleToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhsDoubleToStringNode.execute(lhs), rhs));
    }

    @Specialization
    protected void stringLong(
        VirtualFrame frame,
        byte[] lhs,
        long rhs,
        @Shared("rhsLongToStringNode") @Cached LongToStringNode rhsLongToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhs, rhsLongToStringNode.execute(rhs)));
    }

    @Specialization
    protected void stringDouble(
        VirtualFrame frame,
        byte[] lhs,
        double rhs,
        @Shared("rhsDoubleToStringNode") @Cached DoubleToStringNode rhsDoubleToStringNode,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhs, rhsDoubleToStringNode.execute(rhs)));
    }

    @Specialization
    protected void stringString(
        VirtualFrame frame,
        byte[] lhs,
        byte[] rhs,
        @Shared("stringConcatenateNode") @Cached StringConcatenateNode stringConcatenateNode
    ) {
        genericResult(frame, stringConcatenateNode.execute(lhs, rhs));
    }
}
