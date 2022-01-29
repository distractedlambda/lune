package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import org.lunelang.language.runtime.Nil;

@TypeSystem({boolean.class, long.class, double.class, Nil.class})
public abstract class LuneTypeSystem {
    @TypeCheck(Nil.class)
    public static boolean isNil(Object value) {
        return value == Nil.getInstance();
    }

    @TypeCast(Nil.class)
    public static Nil asNil(Object value)  {
        return Nil.getInstance();
    }
}
