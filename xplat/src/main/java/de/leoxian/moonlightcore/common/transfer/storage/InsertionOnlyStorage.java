package de.leoxian.moonlightcore.common.transfer.storage;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

public interface InsertionOnlyStorage<T extends Resource> extends Storage<T> {
    @Override
    default int extract(Transaction transaction, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default int extract(Transaction transaction, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean canExtract(int index, T resource) {
        return false;
    }

    @Override
    default boolean supportsExtraction() {
        return false;
    }
}
