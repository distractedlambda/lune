package org.lunelang.language.nodes.closure;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.CaptureBox;
import org.lunelang.language.runtime.Nil;

@NodeChild(value = "captureBoxSourceNode", type = SourceNode.class)
public abstract class CaptureBoxDereferenceSourceNode extends SourceNode {
    @Specialization(guards = "box.isNil()")
    protected Nil loadNil(CaptureBox box) {
        return Nil.getInstance();
    }

    @Specialization(guards = "box.isBoolean()")
    protected boolean loadBoolean(CaptureBox box) {
        return box.getBoolean();
    }

    @Specialization(guards = "box.isLong()")
    protected long loadLong(CaptureBox box) {
        return box.getLong();
    }

    @Specialization(guards = "box.isDouble()")
    protected double loadDouble(CaptureBox box) {
        return box.getDouble();
    }

    @Specialization(guards = "box.isObject()")
    protected Object loadObject(CaptureBox box) {
        return box.getObject();
    }
}
