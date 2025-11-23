package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;

public abstract class SingleResourceStorage<T extends TransferResource<?>> extends SnapshotJournal<ResourceStack<T>> implements SingleSlotStorage<T> {
    private final T blankResource;

    public T currentResource;
    public int amount = 0;

    public SingleResourceStorage(T blankResource) {
        if(!blankResource.isBlank()) {
            throw new IllegalArgumentException("Expected a blank resource, but this was given: " + blankResource);
        }

        this.blankResource = blankResource;
        this.currentResource = blankResource;
    }

    public abstract int getCapacity(T resource);

    public void writeToNBT(CompoundTag tag) {
        tag.put("resource", this.currentResource.toNBT());
        tag.putInt("amount", this.amount);
    }

    public boolean canInsert(T resource) {
        return true;
    }

    public boolean canExtract(T resource) {
        return true;
    }

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);

        if((insertedResource.equals(this.currentResource) || this.currentResource.isBlank()) && canInsert(insertedResource)) {
            int insertedAmount = Math.min(maxAmount, getCapacity(insertedResource) - this.amount);

            if(insertedAmount > 0) {
                updateSnapshots(context);

                if(this.currentResource.isBlank()) {
                    this.currentResource = insertedResource;
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
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);

        if(extractedResource.equals(this.currentResource) && canExtract(extractedResource)) {
            int extractedAmount = Math.min(maxAmount, this.amount);

            if(extractedAmount > 0) {
                updateSnapshots(context);
                this.amount -= extractedAmount;

                if(this.amount == 0) {
                    this.currentResource = this.blankResource;
                }

                return extractedAmount;
            }
        }

        return 0;
    }

    @Override
    public ResourceStack<T> createSnapshot() {
        return new ResourceStack<>(this.currentResource, this.amount);
    }

    @Override
    public void revertToSnapshot(ResourceStack<T> snapshot) {
        this.currentResource = snapshot.resource();
        this.amount = snapshot.amount();
    }

    @Override
    public T getResource() {
        return this.currentResource;
    }

    @Override
    public boolean isResourceBlank() {
        return this.currentResource.isBlank();
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return "SingleResourceStorage[%d %s]".formatted(this.amount, this.currentResource);
    }
}
