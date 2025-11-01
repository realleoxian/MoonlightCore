package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;

public interface ExtractionOnlyStorage<T extends TransferResource<?>> extends Storage<T> {

    @Override
    default int insert(Transaction tx, T resource, int amount) {
        return 0;
    }

    @Override
    default boolean supportsInsertion() {
        return false;
    }

}
