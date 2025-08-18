package de.leowgc.moonlightcore.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyStorage;

public final class EnergyStorageImpl implements EnergyStorage {
    private final long capacity;
    private long energy;

    public EnergyStorageImpl(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public int insert(Transaction transaction, TransferResource<Long> resource) {
        if(this.supportsInsertion()) {
            long insertable = Math.min(resource.amount(), capacity - energy);

            if(insertable > 0) {
                transaction.addCloseCallback((state) -> {
                    if(state == Transaction.State.COMMITTED) {
                        this.energy += insertable;
                    }
                });
            }

            return (int) insertable;
        }

        return 0;
    }

    @Override
    public TransferResource<Long> extract(Transaction transaction, Long resourceType, int maxAmount) {
        if(this.supportsExtraction()) {
            long extracted = Math.min(maxAmount, this.energy);

            if(extracted > 0) {
                transaction.addCloseCallback((state) -> {
                    if(state == Transaction.State.COMMITTED) {
                        this.energy -= extracted;
                    }
                });
            }

            return EnergyResource.of(extracted);
        }

        return EnergyResource.empty();
    }
}
