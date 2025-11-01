package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;

public class EmptyStorageView<V, T extends TransferResource<V>> implements StorageView<V, T> {
    private final T emptyResource;
    private final int capacity;

    public EmptyStorageView(T emptyResource, int capacity) {
        this.emptyResource = emptyResource;
        this.capacity = capacity;
    }

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        return 0;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        return 0;
    }

    @Override
    public boolean isResourceValid(T resource) {
        return false;
    }

    @Override
    public int getCapacity(T resource) {
        return this.capacity;
    }

    @Override
    public T resource() {
        return this.emptyResource;
    }

    @Override
    public int amount() {
        return 0;
    }
}
