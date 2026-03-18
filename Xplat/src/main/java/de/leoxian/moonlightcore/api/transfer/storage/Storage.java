package de.leoxian.moonlightcore.api.transfer.storage;

import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;

import java.util.Objects;

public interface Storage<T> {

    @SuppressWarnings("unchecked")
    static <T> Class<Storage<T>> asClass() {
        return (Class<Storage<T>>) (Object) Storage.class;
    }

    int insert(TransactionContext tx, int index, T resource, int maxAmount);

    default int insert(TransactionContext tx, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);

        int inserted = 0;
        for(int i = 0; i < size(); i++) {
            inserted += insert(tx, i, resource, maxAmount - inserted);

            if(inserted == maxAmount) {
                break;
            }
        }

        return inserted;
    }

    default boolean canInsert(int index, T resource) {
        Objects.checkIndex(index, size());
        return true;
    }

    default boolean canInsert(T resource) {
        for(int i = 0; i < size(); i++) {
            if(canInsert(i, resource))
                return true;
        }

        return false;
    }

    default boolean supportsInsertion() {
        return true;
    }

    int extract(TransactionContext tx, int index, T resource, int maxAmount);

    default int extract(TransactionContext tx, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);

        int extracted = 0;
        for(int i = 0; i < size(); i++) {
            extracted += extract(tx, i, resource, maxAmount - extracted);

            if(extracted == 0) {
                break;
            }
        }

        return extracted;
    }

    default boolean canExtract(int index, T resource) {
        Objects.checkIndex(index, size());
        return true;
    }

    default boolean canExtract(T resource) {
        for(int i = 0; i < size(); i++) {
            if(canExtract(i, resource))
                return true;
        }

        return false;
    }

    default boolean supportsExtraction() {
        return true;
    }

    int getCapacity(int index, T resource);

    T getResource(int index);

    int getAmount(int index);

    boolean isBlank(int index);

    int size();

}
