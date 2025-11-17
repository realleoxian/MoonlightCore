package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CombinedStorage<V, T extends TransferResource<V>, S extends Storage<V, T>> implements Storage<V, T> {
    protected S[] storages;
    protected final int[] baseIndex;
    protected final int sizeCache;

    @SuppressWarnings("unchecked")
    public CombinedStorage(List<? extends Storage<V, T>> storages) {
        this((S[]) storages.toArray(Storage[]::new));
    }

    @SafeVarargs
    public CombinedStorage(S... storages) {
        this.storages = storages;
        this.baseIndex = new int[storages.length];

        int index = 0;
        for(int i = 0; i < storages.length; i++) {
            this.baseIndex[i] = index;
            index += storages[i].size();
        }

        this.sizeCache = index;
    }

    public Storage<V, T> getStorageFromIndex(int idx) {
        return this.storages[idx];
    }

    public int getStorageIndex(int idx) {
        Objects.checkIndex(idx, this.sizeCache);

        for(int storageIdx = 0; storageIdx < this.baseIndex.length - 1; storageIdx++) {
            if(idx < this.baseIndex[storageIdx + 1]) {
                return storageIdx;
            }
        }

        return this.baseIndex.length - 1;
    }

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int remaining = amount;
        for(Storage<V, T> storage : this.storages) {
            if(remaining <= 0) {
                break;
            }

            remaining -= storage.insert(tx, resource, amount);
        }

        return amount - remaining;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int remaining = amount;
        for(Storage<V, T> storage : this.storages) {
            if(remaining <= 0) {
                break;
            }

            remaining -= storage.extract(tx, resource, amount);
        }

        return amount - remaining;
    }

    @Override
    public boolean supportsInsertion() {
        for(var storage : this.storages) {
            if(!storage.supportsInsertion()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean supportsExtraction() {
        for(var storage : this.storages) {
            if(!storage.supportsExtraction()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull StorageView<V, T> get(int index) {
        Objects.checkIndex(index, this.size());

        int handlerIndex = this.getStorageIndex(index);
        return this.getStorageFromIndex(handlerIndex).get(index - this.baseIndex[handlerIndex]);
    }

    @Override
    public int size() {
        return this.sizeCache;
    }

    @Override
    public @NotNull Iterator<StorageView<V, T>> iterator() {
        return new CombinedIterator();
    }

    private class CombinedIterator implements Iterator<StorageView<V, T>> {
        final Iterator<Storage<V, T>> storageIterator = Iterators.forArray(storages);

        Iterator<? extends StorageView<V, T>> currentViewIterator = null;

        CombinedIterator() {
            this.advanceCurrentViewIterator();
        }

        @Override
        public boolean hasNext() {
            return this.currentViewIterator != null && this.currentViewIterator.hasNext();
        }

        @Override
        public StorageView<V, T> next() {
            if(!this.hasNext()) {
                throw new NoSuchElementException();
            }

            StorageView<V, T> returned = this.currentViewIterator.next();
            if(!this.currentViewIterator.hasNext()) {
                this.advanceCurrentViewIterator();
            }

            return returned;
        }

        private void advanceCurrentViewIterator() {
            while(storageIterator.hasNext()) {
                this.currentViewIterator = storageIterator.next().iterator();

                if(!this.currentViewIterator.hasNext()) {
                    break;
                }
            }
        }
    }
}
