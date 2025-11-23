package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;

import java.util.Iterator;

public interface SingleSlotStorage<T> extends Storage<T>, StorageView<T> {

    @Override
    default int insert(TransactionContext context, int index, T resource, int maxAmount) {
        return this.insert(context, resource, maxAmount);
    }

    @Override
    default int extract(TransactionContext context, int index, T resource, int maxAmount) {
        return this.extract(context, resource, maxAmount);
    }

    @Override
    default int simulateExtract(TransactionContext context, int index, T resource, int maxAmount) {
        return this.simulateExtract(context, resource, maxAmount);
    }

    @Override
    default int simulateInsert(TransactionContext context, int index, T resource, int maxAmount) {
        return this.simulateInsert(context, resource, maxAmount);
    }

    @Override
    default SingleSlotStorage<T> get(int slot) {
        if(slot != 0) {
            throw new IndexOutOfBoundsException("Slot " + slot + " does not exist in a single-slot storage");
        }

        return this;
    }

    @Override
    default int getAmount(int index) {
        return this.getAmount();
    }

    @Override
    default int getCapacity(int index, T resource) {
        return this.getCapacity(resource);
    }

    @Override
    default boolean isBlank(int index) {
        return this.isResourceBlank();
    }

    @Override
    default ResourceStack<T> toStack(int index) {
        return this.toStack();
    }

    @Override
    default Iterator<StorageView<T>> iterator() {
        return Iterators.singletonIterator(this);
    }

    @Override
    default int size() {
        return 1;
    }

}
