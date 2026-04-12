package de.realleoxian.moonlightcore.api.transfer.storage;

import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;

public interface InsertionOnlyStorage<T> extends Storage<T> {

    @Override
    default int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default int extract(TransactionContext tx, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean canExtract(int index, T resource) {
        return false;
    }

    @Override
    default boolean canExtract(T resource) {
        return false;
    }

    @Override
    default boolean supportsExtraction() {
        return false;
    }

}
