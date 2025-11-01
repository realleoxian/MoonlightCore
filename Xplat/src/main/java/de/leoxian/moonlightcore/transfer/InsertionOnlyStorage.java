package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;

public interface InsertionOnlyStorage<T extends TransferResource<?>> extends Storage<T> {

    @Override
    default int extract(Transaction tx, T resource, int amount) {
        return 0;
    }

    @Override
    default boolean supportsExtraction() {
        return false;
    }

}
