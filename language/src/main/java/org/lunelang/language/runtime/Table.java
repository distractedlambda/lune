package org.lunelang.language.runtime;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.HiddenKey;
import com.oracle.truffle.api.object.Shape;

import static com.oracle.truffle.api.CompilerDirectives.castExact;
import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;

public final class Table extends DynamicObject {
    public Table(Shape shape) {
        super(shape);
    }

    public Object getMetatable() {
        return getShape().getDynamicType();
    }

    public Object getMetatable(DynamicObjectLibrary tables) {
        return tables.getDynamicType(this);
    }

    public long getSequenceLength(DynamicObjectLibrary tables) {
        try {
            return tables.getLongOrDefault(this, SEQUENCE_LENGTH_KEY, 0L);
        } catch (UnexpectedResultException exception) {
            throw shouldNotReachHere(exception);
        }
    }

    public Object[] getSequenceStorage(DynamicObjectLibrary tables) {
        return castExact(tables.getOrDefault(this, SEQUENCE_STORAGE_KEY, null), Object[].class);
    }

    private static final HiddenKey SEQUENCE_LENGTH_KEY = new HiddenKey("sequence length");
    private static final HiddenKey SEQUENCE_STORAGE_KEY = new HiddenKey("sequence storage");
}
