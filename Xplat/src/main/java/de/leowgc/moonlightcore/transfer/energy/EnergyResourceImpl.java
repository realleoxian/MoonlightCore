package de.leowgc.moonlightcore.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyResource;

public final class EnergyResourceImpl implements EnergyResource {
    private long energy;

    public EnergyResourceImpl(long energy) {
        this.energy = energy;
    }

    @Override
    public Long get() {
        return this.energy;
    }

    @Override
    public int amount() {
        return (int) this.energy;
    }

    @Override
    public TransferResource<Long> copy() {
        return new EnergyResourceImpl(this.energy);
    }

}
