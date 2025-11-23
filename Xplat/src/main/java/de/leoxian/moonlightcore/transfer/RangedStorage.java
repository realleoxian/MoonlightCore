package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class RangedStorage<T> implements Storage<T> {
    private final Supplier<Storage<T>> backingStorage;
    private final int start;
    private final int end;

    public RangedStorage(Storage<T> backingStorage, int start, int end) {
        this(() -> backingStorage, start, end);
    }

    public RangedStorage(Supplier<Storage<T>> backingStorage, int start, int end) {
        if(start < 0 || start >= end) {
            throw new IndexOutOfBoundsException("Invalid range: start= %d, end=%d".formatted(start, end));
        }
        if(end > backingStorage.get().size()) {
            throw new IndexOutOfBoundsException("Invalid range: end " + end + " is larger than the size of the storage: " + backingStorage.get().size());
        }

        this.backingStorage = backingStorage;
        this.start = start;
        this.end = end;
    }

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(int i = start; i < end; i++) {
            remaining -= backingStorage.get().insert(context, i, insertedResource, maxAmount);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(int i = start; i < end; i++) {
            remaining -= backingStorage.get().extract(context, i, extractedResource, maxAmount);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public StorageView<T> get(int index) {
        if(index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }

        int actualIndex = start + index;
        return backingStorage.get().get(actualIndex);
    }

    @Override
    public @Nonnull Iterator<StorageView<T>> iterator() {
        return new RangedStorageIterator();
    }

    @Override
    public int size() {
        return end - start;
    }

    private class RangedStorageIterator implements Iterator<StorageView<T>> {
        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size();
        }

        @Override
        public StorageView<T> next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            return get(currentIndex++);
        }
    }
}
