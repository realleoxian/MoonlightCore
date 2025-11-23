package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;

public class BlankResourceView<T extends TransferResource<?>> implements StorageView<T> {
    private final T blankResource;
    private final int capacity;

    public BlankResourceView(T blankResource, int capacity) {
        if(!blankResource.isBlank()) {
            throw new IllegalArgumentException("Expected a blank resource, received " + blankResource);
        }

        this.blankResource = blankResource;
        this.capacity = capacity;
    }

    @Override
    public int extract(TransactionContext context, T insertedResource, int maxAmount) {
        return 0;
    }

    @Override
    public int insert(TransactionContext context, T extractedResource, int maxAmount) {
        return 0;
    }

    @Override
    public T getResource() {
        return this.blankResource;
    }

    @Override
    public int getCapacity(T resource) {
        return this.capacity;
    }

    @Override
    public int getAmount() {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return true;
    }
}
