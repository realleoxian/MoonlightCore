package de.leowgc.moonlightcore.api.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.Storage;
import de.leowgc.moonlightcore.transfer.energy.EmptyEnergyStorageImpl;
import de.leowgc.moonlightcore.transfer.energy.EnergyStorageImpl;

public interface EnergyStorage extends Storage<Long> {

    static Storage<Long> simple(long capacity) {
        return new EnergyStorageImpl(capacity);
    }

    static Storage<Long> empty() {
        return new EmptyEnergyStorageImpl();
    }

}
