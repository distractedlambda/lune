package org.lunelang.language.compiler;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public abstract class VariadicInstruction extends Instruction {
    private static final List<Instruction> EMPTY_VALUES = new ArrayList<>();

    private List<Instruction> values = EMPTY_VALUES;

    public final List<Instruction> getValues() {
        return unmodifiableList(values);
    }

    public final void appendValue(Instruction value) {
        if (values == EMPTY_VALUES) {
            values = new ArrayList<>();
        }

        values.add(value);
    }
}
