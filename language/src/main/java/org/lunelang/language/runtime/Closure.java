package org.lunelang.language.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

public final class Closure extends DynamicObject {
    public Closure(Shape shape) {
        super(shape);
    }

    public CallTarget getCallTarget() {
        return (CallTarget) getShape().getDynamicType();
    }
}
