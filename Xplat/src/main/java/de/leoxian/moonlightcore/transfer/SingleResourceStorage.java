package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;

public abstract class SingleResourceStorage<T extends TransferResource<?>> extends SnapshotJournal<ResourceStack<T>> implements SingleSlotStorage<T> {

    protected final T emptyResource;

    public T resource;
    public int amount = 0;

    protected SingleResourceStorage(T emptyResource) {
        this.emptyResource = emptyResource;
        this.resource = emptyResource;
    }

    protected boolean canInsert(T resource) {
        return true;
    }

    protected boolean canExtract(T resource) {
        return true;
    }

    @Override
    public abstract int getCapacity(T resource);

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(this.isResourceValid(resource) && this.canInsert(resource)) {
            int insertedAmount = Math.min(amount, getCapacity(resource) - this.amount);

            if(insertedAmount > 0) {
                this.updateSnapshots(tx);

                if(resource.isEmpty()) {
                    this.resource = resource;
                    this.amount = insertedAmount;
                } else {
                    this.amount += insertedAmount;
                }

                return insertedAmount;
            }
        }

        return 0;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(this.isResourceValid(resource) && this.canExtract(resource)) {
            int extractedAmount = Math.min(amount, this.amount);

            if(extractedAmount > 0) {
                this.updateSnapshots(tx);
                this.amount -= extractedAmount;

                if(amount <= 0) {
                    this.resource = this.emptyResource;
                }

                return extractedAmount;
            }
        }

        return 0;
    }

    @Override
    public ResourceStack<T> createSnapshot() {
        return new ResourceStack<>(this.resource, this.amount);
    }

    @Override
    public void revertToSnapshot(ResourceStack<T> snapshot) {
        this.resource = snapshot.resource();
        this.amount = snapshot.amount();
    }

    @Override
    public boolean isResourceValid(T resource) {
        return this.resource.isEmpty() || resource.fullyMatches(this.resource.get(), this.resource.getNBT());
    }

    @Override
    public T resource() {
        return this.resource;
    }

    @Override
    public int amount() {
        return this.amount;
    }

}
