package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

@NodeInfo(shortName = "not")
public abstract class NotNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return NotNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void onBoolean(VirtualFrame frame, boolean operand) {
        booleanResult(frame, !operand);
    }

    @Specialization
    protected void onLong(VirtualFrame frame, long operand) {
        booleanResult(frame, false);
    }

    @Specialization
    protected void onDouble(VirtualFrame frame, double operand) {
        booleanResult(frame, false);
    }

    @Specialization(replaces = {"onBoolean", "onLong", "onDouble"})
    protected void onObject(VirtualFrame frame, Object operand) {
        boolean result;

        if (operand instanceof Boolean b) {
            result = !b;
        } else {
            result = isNil(operand);
        }

        booleanResult(frame, result);
    }
}
