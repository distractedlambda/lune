package org.lunelang.language.nodes.instructions;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.InstructionNode;
import org.lunelang.language.nodes.UnaryOpNode;

public abstract class LengthNode extends UnaryOpNode {
    @Override
    public final InstructionNode cloneUninitialized() {
        return LengthNodeGen.create(getResultSlot(), getOperandSlot());
    }

    @Specialization
    protected void onString(VirtualFrame frame, byte[] string) {
        longResult(frame, string.length);
    }
}
