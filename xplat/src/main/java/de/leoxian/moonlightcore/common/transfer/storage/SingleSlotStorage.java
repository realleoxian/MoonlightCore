package de.leoxian.moonlightcore.common.transfer.storage;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

public interface SingleSlotStorage<T extends Resource> extends Storage<T> {
    @Override
    default int insert(Transaction transaction, T resource, int maxAmount) {
        return insert(transaction, 0, resource, maxAmount);
    }

    @Override
    default int extract(Transaction transaction, T resource, int maxAmount) {
        return extract(transaction, 0, resource, maxAmount);
    }

    @Override
    default int size() {
        return 1;
    }
}
