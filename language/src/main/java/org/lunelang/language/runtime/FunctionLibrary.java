package org.lunelang.language.runtime;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;

@GenerateLibrary
public abstract class FunctionLibrary extends Library {
    public abstract Object call(Object receiver, Object arguments);
}
