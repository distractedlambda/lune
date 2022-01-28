package org.lunelang.language.nodes.closure;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import org.lunelang.language.nodes.SourceNode;
import org.lunelang.language.runtime.Closure;
import org.lunelang.language.runtime.Nil;

@NodeField(name = "key", type = Object.class)
@NodeChild(value = "closureSourceNode", type = SourceNode.class)
public abstract class ClosureCaptureSourceNode extends SourceNode {
    protected abstract Object getKey();

    @Specialization(limit = "3")
    protected Object loadCapture(Closure closure, @CachedLibrary("closure") DynamicObjectLibrary closures) {
        return closures.getOrDefault(closure, getKey(), Nil.getInstance());
    }
}
