package org.lunelang.language.nodes.closure;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.lunelang.language.nodes.DestinationNode;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.CaptureBox;
import org.lunelang.language.runtime.Nil;

public abstract class CaptureBoxDereferenceDestinationNode extends DestinationNode {
    @Child private SourceNode captureBoxSourceNode;

    protected CaptureBoxDereferenceDestinationNode(SourceNode captureBoxSourceNode) {
        this.captureBoxSourceNode = captureBoxSourceNode;
    }

    @Override
    public final void executeNilStore(VirtualFrame frame, Nil value) {
        executeNilStore((CaptureBox) captureBoxSourceNode.executeGenericLoad(frame), value);
    }

    @Override
    public final void executeBooleanStore(VirtualFrame frame, boolean value) {
        executeBooleanStore((CaptureBox) captureBoxSourceNode.executeGenericLoad(frame), value);
    }

    @Override
    public final void executeLongStore(VirtualFrame frame, long value) {
        executeLongStore((CaptureBox) captureBoxSourceNode.executeGenericLoad(frame), value);
    }

    @Override
    public final void executeDoubleStore(VirtualFrame frame, double value) {
        executeDoubleStore((CaptureBox) captureBoxSourceNode.executeGenericLoad(frame), value);
    }

    @Override
    public final void executeGenericStore(VirtualFrame frame, Object value) {
        executeGenericStore((CaptureBox) captureBoxSourceNode.executeGenericLoad(frame), value);
    }

    protected abstract void executeNilStore(CaptureBox box, Nil value);

    protected abstract void executeBooleanStore(CaptureBox box, boolean value);

    protected abstract void executeLongStore(CaptureBox box, long value);

    protected abstract void executeDoubleStore(CaptureBox box, double value);

    protected abstract void executeGenericStore(CaptureBox box, Object value);

    @Specialization
    protected void storeNil(CaptureBox box, Nil value) {
        box.setNil();
    }

    @Specialization
    protected void storeBoolean(CaptureBox box, boolean value) {
        box.setBoolean(value);
    }

    @Specialization
    protected void storeLong(CaptureBox box, long value) {
        box.setLong(value);
    }

    @Specialization
    protected void storeDouble(CaptureBox box, double value) {
        box.setDouble(value);
    }

    @Fallback
    protected void storeObject(CaptureBox box, Object value) {
        box.setObject(value);
    }
}
