package org.lunelang.language.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import static org.lunelang.language.nodes.LuneTypeSystemGen.expectBoolean;
import static org.lunelang.language.nodes.LuneTypeSystemGen.expectDouble;
import static org.lunelang.language.nodes.LuneTypeSystemGen.expectLong;

public abstract class SourceNode extends LuneNode {
    public abstract Object executeGenericLoad(VirtualFrame frame);

    public boolean executeBooleanLoad(VirtualFrame frame) throws UnexpectedResultException {
        return expectBoolean(executeGenericLoad(frame));
    }

    public long executeLongLoad(VirtualFrame frame) throws UnexpectedResultException {
        return expectLong(executeGenericLoad(frame));
    }

    public double executeDoubleLoad(VirtualFrame frame) throws UnexpectedResultException {
        return expectDouble(executeGenericLoad(frame));
    }
}
