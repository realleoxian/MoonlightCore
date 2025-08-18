package de.leowgc.moonlightcore.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyStorage;
import net.minecraft.nbt.CompoundTag;

public final class EmptyEnergyStorageImpl implements EnergyStorage {

    @Override
    public int insert(Transaction transaction, TransferResource<Long> resource) {
        return 0;
    }

    @Override
    public TransferResource<Long> extract(Transaction transaction, Long resourceType, int maxAmount) {
        return EnergyResource.empty();
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag();
    }

    @Override
    public void fromNBT(CompoundTag nbt) {

    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

}
