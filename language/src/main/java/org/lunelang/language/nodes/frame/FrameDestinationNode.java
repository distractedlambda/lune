package org.lunelang.language.nodes.frame;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.DestinationNode;
import org.lunelang.language.runtime.Nil;

@NodeField(name = "slot", type = int.class)
public abstract class FrameDestinationNode extends DestinationNode {
    public abstract int getSlot();

    @Specialization
    protected void storeNil(VirtualFrame frame, Nil value) {
        frame.clear(getSlot());
    }

    @Specialization
    protected void storeBoolean(VirtualFrame frame, boolean value) {
        frame.setBoolean(getSlot(), value);
    }

    @Specialization
    protected void storeLong(VirtualFrame frame, long value) {
        frame.setLong(getSlot(), value);
    }

    @Specialization
    protected void storeDouble(VirtualFrame frame, double value) {
        frame.setDouble(getSlot(), value);
    }

    @Fallback
    protected void storeObject(VirtualFrame frame, Object value) {
        frame.setObject(getSlot(), value);
    }

    @Override
    public final void executeNilStore(VirtualFrame frame, Nil value) {
        frame.clear(getSlot());
    }
}
