package de.leoxian.moonlightcore.common.transfer.storage;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

public interface ExtractionOnlyStorage<T extends Resource> extends Storage<T> {
    @Override
    default int insert(Transaction transaction, int index, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default int insert(Transaction transaction, T resource, int maxAmount) {
        return 0;
    }

    @Override
    default boolean canInsert(int index, T resource) {
        return false;
    }


    @Override
    default boolean supportsInsertion() {
        return false;
    }
}
