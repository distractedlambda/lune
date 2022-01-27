package org.lunelang.language.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.runtime.Nil;

public abstract class DestinationNode extends LuneNode {
    public abstract void executeGenericStore(VirtualFrame frame, Object value);

    public void executeNilStore(VirtualFrame frame, Nil value) {
        executeGenericStore(frame, Nil.getInstance());
    }

    public void executeBooleanStore(VirtualFrame frame, boolean value) {
        executeGenericStore(frame, value);
    }

    public void executeLongStore(VirtualFrame frame, long value) {
        executeGenericStore(frame, value);
    }

    public void executeDoubleStore(VirtualFrame frame, double value) {
        executeGenericStore(frame, value);
    }
}
