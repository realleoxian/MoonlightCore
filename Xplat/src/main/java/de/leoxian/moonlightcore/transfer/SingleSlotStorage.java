package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface SingleSlotStorage<T extends TransferResource<?>> extends Storage<T>, StorageView<T> {

    @Override
    default int extract(Transaction tx, int index, T resource, int amount) {
        return this.extract(tx, resource, amount);
    }

    @Override
    default int insert(Transaction tx, int index, T resource, int amount) {
        return this.insert(tx, resource, amount);
    }

    @Override
    default int size() {
        return 1;
    }

    @Override
    default int getAmount(int index) {
        return this.amount();
    }

    @Override
    default T getResource(int index) {
        return this.resource();
    }

    @Override
    default boolean isResourceValid(int index, T resource) {
        return this.isResourceValid(resource);
    }

    @Override
    default int getLimit(int index, T resource) {
        return this.getCapacity(resource);
    }

    @Override
    default @NotNull StorageView<T> get(int index) {
        return this;
    }

    @Override
    default @NotNull Iterator<StorageView<T>> iterator() {
        return StorageInternals.singletonIterator(this);
    }
    
}
