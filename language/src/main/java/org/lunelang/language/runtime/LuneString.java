package org.lunelang.language.runtime;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.oracle.truffle.api.CompilerAsserts.neverPartOfCompilation;

public final class LuneString {
    @CompilationFinal(dimensions = 1) private final byte[] bytes;

    public LuneString(byte[] bytes) {
        assert bytes != null;
        this.bytes = bytes;
    }

    public LuneString(String string) {
        neverPartOfCompilation();
        bytes = string.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int hashCode() {
        neverPartOfCompilation();
        return (int) FxHash.hash(bytes);
    }

    @Override
    public boolean equals(Object object) {
        neverPartOfCompilation();

        if (!(object instanceof LuneString other)) {
            return false;
        }

        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public String toString() {
        neverPartOfCompilation();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
