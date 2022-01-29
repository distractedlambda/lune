package org.lunelang.language.nodes.closure;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.profiles.BranchProfile;
import org.lunelang.language.nodes.DestinationNode;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Nil;

import static org.lunelang.language.nodes.LuneTypeSystem.isNil;

public final class ClosureCaptureDestinationNode extends DestinationNode {
    @Child private SourceNode closureSourceNode;
    private final Object key;

    private final DynamicObjectLibrary closures = DynamicObjectLibrary.getFactory().createDispatched(3);
    private final BranchProfile nilEncountered = BranchProfile.create();
    private final BranchProfile nonNilEncountered = BranchProfile.create();

    public ClosureCaptureDestinationNode(SourceNode closureSourceNode, Object key) {
        this.closureSourceNode = closureSourceNode;
        this.key = key;
    }

    private Closure getClosure(VirtualFrame frame) {
        return (Closure) closureSourceNode.executeGenericLoad(frame);
    }

    @Override
    public void executeNilStore(VirtualFrame frame, Nil value) {
        nilEncountered.enter();
        closures.removeKey(getClosure(frame), key);
    }

    @Override
    public void executeBooleanStore(VirtualFrame frame, boolean value) {
        nonNilEncountered.enter();
        closures.put(getClosure(frame), key, value);
    }

    @Override
    public void executeLongStore(VirtualFrame frame, long value) {
        nonNilEncountered.enter();
        closures.putLong(getClosure(frame), key, value);
    }

    @Override
    public void executeDoubleStore(VirtualFrame frame, double value) {
        nonNilEncountered.enter();
        closures.putDouble(getClosure(frame), key, value);
    }

    @Override
    public void executeGenericStore(VirtualFrame frame, Object value) {
        var closure = getClosure(frame);
        if (isNil(value)) {
            nilEncountered.enter();
            closures.removeKey(closure, key);
        } else {
            closures.put(closure, key, value);
        }
    }
}
