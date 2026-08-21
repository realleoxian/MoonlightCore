package de.leoxian.moonlightcore.common.transfer.storage;

import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;

public interface Storage<T extends Resource> {
    @SuppressWarnings("unchecked")
    static <T extends Resource> Class<Storage<T>> asClass() {
        return (Class<Storage<T>>) (Class<?>) Storage.class;
    }

    int insert(Transaction transaction, int index, T resource, int maxAmount);

    default int insert(Transaction transaction, T resource, int maxAmount) {
        int inserted = 0;
        for (int i = 0; i < size(); i++) {
            inserted += insert(transaction, i, resource, maxAmount - inserted);
            if (inserted == maxAmount) {
                break;
            }
        }
        return inserted;
    }

    default boolean canInsert(int index, T resource) {
        return true;
    }

    default boolean supportsInsertion() {
        return true;
    }

    int extract(Transaction transaction, int index, T resource, int maxAmount);

    default int extract(Transaction transaction, T resource, int maxAmount) {
        int extracted = 0;
        for (int i = 0; i < size(); i++) {
            extracted += extract(transaction, i, resource, maxAmount - extracted);
            if (extracted == maxAmount) {
                break;
            }
        }
        return extracted;
    }

    default boolean canExtract(int index, T resource) {
        return true;
    }

    default boolean supportsExtraction() {
        return true;
    }

    T getResource(int index);

    int getAmount(int index);

    int getCapacity(int index, T resource);

    int size();
}
