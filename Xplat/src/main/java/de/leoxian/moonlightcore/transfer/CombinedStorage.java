package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;

import java.util.*;

public class CombinedStorage<T, S extends Storage<T>> implements Storage<T> {
    protected S[] parts;
    protected final int[] baseIndex;
    protected final int sizeCache;

    @SuppressWarnings("unchecked")
    public CombinedStorage(List<? extends Storage<T>> parts) {
        this((S[]) parts.toArray(Storage[]::new));
    }

    @SafeVarargs
    public CombinedStorage(S... parts) {
        this.parts = parts;
        this.baseIndex = new int[parts.length];

        int idx = 0;
        for(int i = 0; i < parts.length; i++) {
            this.baseIndex[i] = idx;
            idx += parts[i].size();
        }
        this.sizeCache = idx;
    }

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(S part : parts) {
            remaining -= part.insert(context, insertedResource, maxAmount - remaining);

            if(remaining == 0) {
                 break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public boolean supportsInsertion() {
        for(S part : parts) {
            if(part.supportsInsertion()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(S part : parts) {
            remaining -= part.extract(context, extractedResource, maxAmount - remaining);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public boolean supportsExtraction() {
        for(S part : parts) {
            if(part.supportsExtraction()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nonnull StorageView<T> get(int index) {
        return null;
    }

    public int getStorageBaseIndex(int index) {
        Objects.checkIndex(index, this.sizeCache);

        for(int i = 0; i < this.baseIndex.length; i++) {
            int nextBase = (i < this.baseIndex.length - 1) ? this.baseIndex[i + 1] : this.sizeCache;

            if(index < nextBase) {
                return this.baseIndex[i];
            }
        }

        // This should never happen
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return this.sizeCache;
    }

    @Override
    public @Nonnull Iterator<StorageView<T>> iterator() {
        return new CombinedIterator();
    }

    @Override
    public String toString() {
        StringJoiner partNames = new StringJoiner(", ");

        for(S part : parts) {
            partNames.add(part.toString());
        }

        return "CombinedStorage[" + partNames + "]";
    }

    private class CombinedIterator implements Iterator<StorageView<T>> {
        final Iterator<S> partIterator = Iterators.forArray(parts);
        Iterator<? extends StorageView<T>> currentPartIterator = null;

        @Override
        public boolean hasNext() {
            return currentPartIterator != null && currentPartIterator.hasNext();
        }

        @Override
        public StorageView<T> next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }

            StorageView<T> returned = currentPartIterator.next();
            if(!currentPartIterator.hasNext()) {
                advanceCurrentPartIterator();
            }

            return returned;
        }

        private void advanceCurrentPartIterator() {
            while(partIterator.hasNext()) {
                this.currentPartIterator = partIterator.next().iterator();

                if(this.currentPartIterator.hasNext()) {
                    break;
                }
            }
        }
    }
}
