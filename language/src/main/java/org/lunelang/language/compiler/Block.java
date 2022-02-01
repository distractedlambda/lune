package org.lunelang.language.compiler;

import org.graalvm.collections.EconomicSet;

public final class Block {
    private final Function function;
    private Instruction firstInstruction, lastInstruction;
    private EconomicSet<Block> predecessors;

    public Block(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public <I extends Instruction> I append(I instruction) {
        assert instruction.getBlock() == null;
        assert instruction.getNext() == null;
        assert instruction.getPrior() == null;

        instruction.setBlock(this);
        instruction.setPrior(lastInstruction);

        if (lastInstruction != null) {
            assert firstInstruction != null;
            lastInstruction.setNext(instruction);
        } else {
            assert firstInstruction == null;
            firstInstruction = instruction;
        }

        lastInstruction = instruction;
        return instruction;
    }

    public Instruction getFirstInstruction() {
        return firstInstruction;
    }

    public Instruction getLastInstruction() {
        return lastInstruction;
    }

    public void setFirstInstruction(Instruction firstInstruction) {
        this.firstInstruction = firstInstruction;
    }

    public void setLastInstruction(Instruction lastInstruction) {
        this.lastInstruction = lastInstruction;
    }

    public void addPredecessor(Block predecessor) {
        if (predecessors == null) {
            predecessors = EconomicSet.create();
        }

        predecessors.add(predecessor);
    }

    public void removePredecessor(Block predecessor) {
        predecessors.remove(predecessor);
    }
}
