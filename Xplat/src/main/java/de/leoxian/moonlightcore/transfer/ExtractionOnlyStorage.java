package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;

public interface ExtractionOnlyStorage<T> extends Storage<T> {

    @Override
    default int insert(TransactionContext context, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean supportsInsertion() {
        return false;
    }

}
