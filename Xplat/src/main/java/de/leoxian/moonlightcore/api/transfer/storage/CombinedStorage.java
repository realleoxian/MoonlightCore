package de.leoxian.moonlightcore.api.transfer.storage;

import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CombinedStorage<T, S extends Storage<T>> implements Storage<T> {

    private final S[] storages;
    private final int[] baseIndices;
    private final int cachedSize;

    @SuppressWarnings("unchecked")
    public CombinedStorage(List<S> storages) {
        this((S[]) storages.toArray(Object[]::new));
    }

    @SafeVarargs
    public CombinedStorage(S... storages) {
        this.storages = storages;
        this.baseIndices = new int[storages.length];

        int index = 0;
        for(int i = 0; i < storages.length; i++) {
            this.baseIndices[i] = index;
            index += storages[i].size();
        }
        this.cachedSize = index;
    }

    @Override
    public int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);

        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].insert(tx, getStorageSlotIndex(storageIndex, index), resource, maxAmount);
    }

    @Override
    public int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);

        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].extract(tx, getStorageSlotIndex(storageIndex, index), resource, maxAmount);
    }

    @Override
    public int getCapacity(int index, T resource) {
        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].getCapacity(getStorageSlotIndex(storageIndex, index), resource);
    }

    @Override
    public T getResource(int index) {
        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].getResource(getStorageSlotIndex(storageIndex, index));
    }

    @Override
    public int getAmount(int index) {
        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].getAmount(getStorageSlotIndex(storageIndex, index));
    }

    @Override
    public boolean isBlank(int index) {
        int storageIndex = getStorageIndexFrom(index);
        return storages[storageIndex].isBlank(getStorageSlotIndex(storageIndex, index));
    }

    @Override
    public int size() {
        return cachedSize;
    }

    private int getStorageIndexFrom(int index) {
        Objects.checkIndex(index, size());

        for(int i = 0; i < baseIndices.length - 1; i++) {
            if(index < baseIndices[i + 1]) {
                return i;
            }
        }

        return baseIndices.length - 1;
    }

    private int getStorageSlotIndex(int storageIndex, int index) {
        return index - baseIndices[storageIndex];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(", ");
        for(S storage : storages) {
            sb.append(storage);
        }

        return "CombinedStorage[%s]".formatted(sb.toString());
    }
}
