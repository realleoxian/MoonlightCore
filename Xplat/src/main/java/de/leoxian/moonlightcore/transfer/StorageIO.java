package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;

public interface StorageIO<T> {

    int insert(TransactionContext context, T insertedResource, int maxAmount);

    int extract(TransactionContext context, T extractedResource, int maxAmount);

    default int simulateInsert(TransactionContext context, T insertedResource, int maxAmount) {
        return StorageUtils.simulateInsert(context, this, insertedResource, maxAmount);
    }

    default int simulateExtract(TransactionContext context, T extractedResource, int maxAmount) {
        return StorageUtils.simulateExtract(context, this, extractedResource, maxAmount);
    }

}
