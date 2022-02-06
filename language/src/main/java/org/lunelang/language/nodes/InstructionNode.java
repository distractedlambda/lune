package org.lunelang.language.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class InstructionNode extends LuneNode {
    public abstract void execute(VirtualFrame frame);

    public abstract InstructionNode cloneUninitialized();
}
