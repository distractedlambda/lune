package org.lunelang.language.nodes;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class ReturnException extends ControlFlowException {
    private final Object value;

    public ReturnException(Object value) {
        assert value != null;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
