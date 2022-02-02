package org.lunelang.language.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

public final class Table extends DynamicObject {
    public Table(Shape shape) {
        super(shape);
    }

    public Object getMetatable() {
        return getShape().getDynamicType();
    }
}
