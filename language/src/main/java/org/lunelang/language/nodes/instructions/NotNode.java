package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.UnaryOpNode;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

public abstract class NotNode extends UnaryOpNode {
    protected NotNode(int resultSlot, int operandSlot) {
        super(resultSlot, operandSlot);
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
        if (operand instanceof Boolean b) {
            booleanResult(frame, !b);
        } else {
            booleanResult(frame, isNil(operand));
        }
    }
}
