package de.realleoxian.moonlightcore.api.transfer.storage;

import de.realleoxian.moonlightcore.api.transfer.Resource;
import de.realleoxian.moonlightcore.api.transfer.ResourceStack;
import de.realleoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.realleoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.nbt.CompoundTag;

public abstract class SingleResourceStorage<T extends Resource<?>> extends SnapshotJournal<ResourceStack<T>> implements Storage<T> {
    protected static final String TAG_RESOURCE = "resource";
    protected static final String TAG_AMOUNT = "amount";

    private final T blankResource;
    public T currentResource;
    public int currentAmount;

    public SingleResourceStorage(T blankResource) {
        if(!blankResource.isBlank()) throw new IllegalArgumentException("Expected a blank resource, instead got: " + blankResource);
        this.blankResource = blankResource;
        this.currentResource = blankResource;
        this.currentAmount = 0;
    }

    public abstract void loadFromNBT(CompoundTag nbt);

    public abstract void writeToNBT(CompoundTag nbt);

    @Override
    public int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        StoragePreconditions.singleSlotIndexCheck(index);
        StoragePreconditions.notNegative(maxAmount);

        if((isBlank(index) || currentResource == resource) && canInsert(index,  resource) && supportsInsertion()) {
            int inserted = Math.min(maxAmount, getCapacity(index, resource) - currentAmount);

            if(inserted > 0) {
                updateSnapshots(tx);

                currentAmount = currentResource.isBlank() ? inserted : currentAmount + inserted;
                currentResource = currentResource.isBlank() ? resource : currentResource;
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        StoragePreconditions.singleSlotIndexCheck(index);
        StoragePreconditions.notNegative(maxAmount);

        if((!isBlank(index) || currentResource == resource) && canExtract(index, resource) && supportsExtraction()) {
            int extracted = Math.min(maxAmount, currentAmount);

            if(extracted > 0) {
                updateSnapshots(tx);

                currentAmount -= extracted;
                currentResource = currentAmount == 0 ? blankResource : currentResource;
                return extracted;
            }
        }

        return 0;
    }

    @Override
    public T getResource(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return currentResource;
    }

    @Override
    public int getAmount(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return currentAmount;
    }

    @Override
    public boolean isBlank(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return currentResource.isBlank();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ResourceStack<T> createSnapshot() {
        return new ResourceStack<>(currentResource, currentAmount);
    }

    @Override
    public void revertToSnapshot(ResourceStack<T> snapshot) {
        currentResource = snapshot.resource();
        currentAmount = snapshot.amount();
    }
}
