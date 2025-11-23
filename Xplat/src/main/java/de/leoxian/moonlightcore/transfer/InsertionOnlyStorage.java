package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;

public interface InsertionOnlyStorage<T> extends Storage<T> {

    @Override
    default int extract(TransactionContext context, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean supportsExtraction() {
        return false;
    }

}
