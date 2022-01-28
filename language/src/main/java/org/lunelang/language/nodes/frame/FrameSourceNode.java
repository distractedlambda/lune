package org.lunelang.language.nodes.frame;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.Nil;

@NodeField(name = "slot", type = int.class)
public abstract class FrameSourceNode extends SourceNode {
    public abstract int getSlot();

    @Specialization(guards = "frame.isBoolean(slot)")
    protected boolean loadBoolean(VirtualFrame frame) {
        return frame.getBoolean(getSlot());
    }

    @Specialization(guards = "frame.isLong(slot)")
    protected long loadLong(VirtualFrame frame) {
        return frame.getLong(getSlot());
    }

    @Specialization(guards = "frame.isDouble(slot)")
    protected double loadDouble(VirtualFrame frame) {
        return frame.getDouble(getSlot());
    }

    @Specialization(guards = "frame.isObject(slot)")
    protected Object loadObject(VirtualFrame frame) {
        return frame.getObject(getSlot());
    }

    @Fallback
    protected Nil loadNil() {
        return Nil.getInstance();
    }
}
