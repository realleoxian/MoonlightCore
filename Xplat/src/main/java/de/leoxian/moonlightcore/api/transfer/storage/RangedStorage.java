package de.leoxian.moonlightcore.api.transfer.storage;

import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;

import java.util.function.Supplier;

public class RangedStorage<T> implements Storage<T> {

    private final Supplier<Storage<T>> backingStorage;
    private final int min;
    private final int max;

    public RangedStorage(Supplier<Storage<T>> backingStorage, int min, int max) {
        if(min < 0 || min >= max) {
            throw new IllegalArgumentException("Invalid Range. min=%d, max=%d".formatted(min, max));
        } else if (max >= backingStorage.get().size()) {
            throw new IllegalArgumentException("Invalid Range. max " + max + " is larger than the size of the storage: " + backingStorage.get().size());
        }

        this.backingStorage = backingStorage;
        this.min = min;
        this.max = max;
    }

    @Override
    public int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    public int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    public int getCapacity(int index, T resource) {
        return 0;
    }

    @Override
    public T getResource(int index) {
        return null;
    }

    @Override
    public int getAmount(int index) {
        return 0;
    }

    @Override
    public boolean isBlank(int index) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

}
