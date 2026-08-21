package de.leoxian.moonlightcore.common.transfer.storage;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.resource.ResourceStack;
import de.leoxian.moonlightcore.common.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.common.util.ValueIOSerializable;
import de.leoxian.moonlightcore.internal.common.transfer.StorageInternals;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class SingleResourceStorage<T extends Resource> extends SnapshotJournal<ResourceStack<T>> implements SingleSlotStorage<T>, ValueIOSerializable {
    public static final String VALUE_IO_RESOURCE_KEY = "moonlightcore:resource";
    public static final String VALUE_IO_AMOUNT_KEY = "moonlightcore:resource_amount";

    private T resource = getEmptyResource();
    private int amount = 0;

    protected abstract T getEmptyResource();
    protected abstract Codec<T> getResourceCodec();

    protected void onContentChanged(ResourceStack<T> oldStack, ResourceStack<T> newStack) {

    }

    public T getResource() {
        return this.resource;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.store(VALUE_IO_RESOURCE_KEY, getResourceCodec(), this.resource);
        output.putInt(VALUE_IO_AMOUNT_KEY, this.amount);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.resource = input.read(VALUE_IO_RESOURCE_KEY, this.getResourceCodec()).orElse(getEmptyResource());
        this.amount = input.getIntOr(VALUE_IO_AMOUNT_KEY, 0);
    }

    @Override
    protected ResourceStack<T> createSnapshot() {
        return new ResourceStack<>(this.resource, this.amount);
    }

    @Override
    protected void readSnapshot(ResourceStack<T> snapshot) {
        this.resource = snapshot.resource();
        this.amount = snapshot.amount();
    }

    @Override
    protected void onRootCommit(ResourceStack<T> originalState) {
        onContentChanged(originalState, createSnapshot());
    }

    @Override
    public int insert(Transaction transaction, int index, T resource, int maxAmount) {
        StorageInternals.checkSingleSlotIndex(index);
        StorageInternals.checkNotEmpty(resource);
        StorageInternals.checkNotNegative(maxAmount);

        if ((this.resource.isEmpty() || this.resource.equals(resource)) && canInsert(index, resource) && supportsInsertion()) {
            int capacity = getCapacity(index, resource);
            int inserted = Math.min(maxAmount, capacity - this.amount);

            if (inserted > 0) {
                updateSnapshots(transaction);

                if (this.resource.isEmpty()) {
                    this.resource = resource;
                    this.amount = inserted;
                } else {
                    this.amount += inserted;
                }
                return inserted;
            }
        }
        return 0;
    }

    @Override
    public int extract(Transaction transaction, int index, T resource, int maxAmount) {
        StorageInternals.checkSingleSlotIndex(index);
        StorageInternals.checkNotEmpty(resource);
        StorageInternals.checkNotNegative(maxAmount);

        if ((this.amount > 0 && this.resource.equals(resource)) && canExtract(index, resource) && supportsExtraction()) {
            int extracted = Math.min(maxAmount, this.amount);

            if (extracted > 0) {
                updateSnapshots(transaction);
                this.amount -= extracted;

                if (this.amount == 0) {
                    this.resource = getEmptyResource();
                }
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public T getResource(int index) {
        StorageInternals.checkSingleSlotIndex(index);
        return this.resource;
    }

    @Override
    public int getAmount(int index) {
        StorageInternals.checkSingleSlotIndex(index);
        return this.amount;
    }
}