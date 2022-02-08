package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import java.nio.charset.StandardCharsets;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;

public final class InternedString {
    @CompilationFinal(dimensions = 1) private final byte[] bytes;
    private final int hashCode;

    public InternedString(byte[] bytes, int hashCode) {
        assert bytes != null;
        this.bytes = bytes;
        this.hashCode = hashCode;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    @Override
    public String toString() {
        neverPartOfCompilation();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
