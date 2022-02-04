package org.lunelang.language.compiler;

public final class IndexedStoreInstruction extends Instruction {
    private Instruction receiver, key, value;

    public IndexedStoreInstruction(Instruction receiver, Instruction key, Instruction value) {
        setReceiver(receiver);
        setKey(key);
        setValue(value);
    }

    public Instruction getReceiver() {
        return receiver;
    }

    public Instruction getKey() {
        return key;
    }

    public Instruction getValue() {
        return value;
    }

    public void setReceiver(Instruction receiver) {
        this.receiver = receiver;
    }

    public void setKey(Instruction key) {
        this.key = key;
    }

    public void setValue(Instruction value) {
        this.value = value;
    }
}
