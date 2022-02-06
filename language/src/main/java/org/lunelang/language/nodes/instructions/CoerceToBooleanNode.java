package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

public abstract class CoerceToBooleanNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return CoerceToBooleanNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void coerceBoolean(VirtualFrame frame, boolean operand) {
        booleanResult(frame, operand);
    }

    @Specialization
    protected void coerceLong(VirtualFrame frame, long operand) {
        booleanResult(frame, true);
    }

    @Specialization
    protected void coerceDouble(VirtualFrame frame, double operand) {
        booleanResult(frame, true);
    }

    @Specialization(replaces = {"coerceBoolean", "coerceLong", "coerceDouble"})
    protected void coerceObject(VirtualFrame frame, Object operand) {
        if (operand instanceof Boolean b) {
            booleanResult(frame, b);
        } else {
            booleanResult(frame, !isNil(operand));
        }
    }
}
