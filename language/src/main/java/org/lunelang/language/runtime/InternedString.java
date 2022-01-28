package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class InternedString {
    @CompilationFinal(dimensions = 1) private final byte[] bytes;

    public InternedString(byte[] bytes) {
        assert bytes != null;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
