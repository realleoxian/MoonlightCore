package de.leowgc.moonlightcore.api.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.transfer.energy.EnergyResourceImpl;

public interface EnergyResource extends TransferResource<Long> {

    static TransferResource<Long> of(long energy) {
        return new EnergyResourceImpl(energy);
    }

    static TransferResource<Long> empty() {
        return of(0);
    }

}
