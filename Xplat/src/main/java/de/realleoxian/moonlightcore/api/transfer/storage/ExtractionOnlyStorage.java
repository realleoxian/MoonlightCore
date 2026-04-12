package de.realleoxian.moonlightcore.api.transfer.storage;

import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;

public interface ExtractionOnlyStorage<T> extends Storage<T> {

    @Override
    default int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default int insert(TransactionContext tx, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean canInsert(int index, T resource) {
        return false;
    }

    @Override
    default boolean canInsert(T resource) {
        return false;
    }

    @Override
    default boolean supportsInsertion() {
        return false;
    }

}
