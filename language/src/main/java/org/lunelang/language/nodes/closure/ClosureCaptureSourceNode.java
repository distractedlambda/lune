package org.lunelang.language.nodes.closure;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Nil;

public final class ClosureCaptureSourceNode extends SourceNode {
    @Child private SourceNode closureSourceNode;
    private final Object key;

    private final DynamicObjectLibrary closures = DynamicObjectLibrary.getFactory().createDispatched(3);

    public ClosureCaptureSourceNode(SourceNode closureSourceNode, Object key) {
        this.closureSourceNode = closureSourceNode;
        this.key = key;
    }

    private Closure getClosure(VirtualFrame frame) {
        return (Closure) closureSourceNode.executeGenericLoad(frame);
    }

    @Override
    public long executeLongLoad(VirtualFrame frame) throws UnexpectedResultException {
        return closures.getLongOrDefault(getClosure(frame), key, Nil.getInstance());
    }

    @Override
    public double executeDoubleLoad(VirtualFrame frame) throws UnexpectedResultException {
        return closures.getDoubleOrDefault(getClosure(frame), key, Nil.getInstance());
    }

    @Override
    public Object executeGenericLoad(VirtualFrame frame) {
        return closures.getOrDefault(getClosure(frame), key, Nil.getInstance());
    }
}
