package org.lunelang.language.compiler;

import org.graalvm.collections.EconomicSet;

public final class LocalVariable {
    private EconomicSet<LocalLoadInstruction> loads;
    private EconomicSet<LocalStoreInstruction> stores;

    public void addLoad(LocalLoadInstruction instruction) {
        if (loads == null) {
            loads = EconomicSet.create();
        }

        loads.add(instruction);
    }

    public void removeLoad(LocalLoadInstruction instruction) {
        loads.remove(instruction);
    }

    public void addStore(LocalStoreInstruction instruction) {
        if (stores == null) {
            stores = EconomicSet.create();
        }

        stores.add(instruction);
    }

    public void removeStore(LocalStoreInstruction instruction) {
        stores.remove(instruction);
    }
}
