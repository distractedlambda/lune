package org.lunelang.language.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.HiddenKey;
import com.oracle.truffle.api.object.Shape;

public final class Table extends DynamicObject {
    public Table(Shape shape) {
        super(shape);
    }

    public static final HiddenKey METATABLE = new HiddenKey("metatable");
}
