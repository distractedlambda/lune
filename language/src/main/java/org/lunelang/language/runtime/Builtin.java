package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeFactory;
import org.lunelang.language.nodes.LuneNode;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;
import static java.util.Objects.requireNonNull;

public final class Builtin {
    private final NodeFactory<BodyNode> bodyNodeFactory;
    private final BodyNode uncachedBodyNode;

    public Builtin(NodeFactory<BodyNode> bodyNodeFactory) {
        neverPartOfCompilation();
        this.bodyNodeFactory = requireNonNull(bodyNodeFactory);
        this.uncachedBodyNode = requireNonNull(bodyNodeFactory.getUncachedInstance());
    }

    public BodyNode createBodyNode() {
        return bodyNodeFactory.createNode();
    }

    @TruffleBoundary
    public Object call(Object arguments) {
        return uncachedBodyNode.executeCall(arguments);
    }

    public static abstract class BodyNode extends LuneNode {
        public abstract Object executeCall(Object arguments);
    }
}
