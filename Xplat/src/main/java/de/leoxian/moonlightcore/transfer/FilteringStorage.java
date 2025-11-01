package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Supplier;

public abstract class FilteringStorage<V, T extends TransferResource<V>> implements Storage<V, T> {

    public static <V, T extends TransferResource<V>> Storage<V, T> insertOnlyOf(Storage<V, T> backingStorage) {
        return of(backingStorage, true, false);
    }

    public static <V, T extends TransferResource<V>> Storage<V, T> extractOnlyOf(Storage<V, T> backingStorage) {
        return of(backingStorage, false, true);
    }

    public static <V, T extends TransferResource<V>> Storage<V, T> readOnlyOf(Storage<V, T> backingStorage) {
        return of(backingStorage, false, false);
    }

    public static <V, T extends TransferResource<V>> Storage<V, T> of(Storage<V, T> backingStorage, boolean allowInsert, boolean allowExtract) {
        if(allowInsert && allowExtract) {
            return backingStorage;
        }

        return new FilteringStorage<>(() -> backingStorage) {
            @Override
            protected boolean canInsert(T resource) {
                return allowInsert;
            }

            @Override
            protected boolean canExtract(T resource) {
                return allowExtract;
            }

            @Override
            public boolean supportsInsertion() {
                return allowInsert && super.supportsInsertion();
            }

            @Override
            public boolean supportsExtraction() {
                return allowExtract && super.supportsExtraction();
            }
        };
    }

    protected final Supplier<Storage<V, T>> backingStorage;

    protected FilteringStorage(Supplier<Storage<V, T>> backingStorage) {
        this.backingStorage = backingStorage;
    }

    protected abstract boolean canInsert(T resource);

    protected abstract boolean canExtract(T resource);

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        if(this.canInsert(resource)) {
            return this.backingStorage.get().insert(tx, resource, amount);
        }

        return 0;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        if(this.canExtract(resource)) {
            return this.backingStorage.get().extract(tx, resource, amount);
        }

        return 0;
    }

    @Override
    public @NotNull StorageView<V, T> get(int index) {
        return this.backingStorage.get().get(index);
    }

    @Override
    public boolean supportsInsertion() {
        return this.backingStorage.get().supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return this.backingStorage.get().supportsExtraction();
    }

    @Override
    public int size() {
        return this.backingStorage.get().size();
    }

    @Override
    public @NotNull Iterator<StorageView<V, T>> iterator() {
        return Iterators.transform(this.backingStorage.get().iterator(), FilteringStorageView::new);
    }

    private class FilteringStorageView implements StorageView<V, T> {
        private final StorageView<V, T> backingView;

        private FilteringStorageView(StorageView<V, T> backingView) {
            this.backingView = backingView;
        }

        @Override
        public int extract(Transaction tx, T resource, int amount) {
            if(!canExtract(resource)) {
                return this.backingView.extract(tx, resource, amount);
            }

            return 0;
        }

        @Override
        public int insert(Transaction tx, T resource, int amount) {
            if(!canInsert(resource)) {
                return this.backingView.insert(tx, resource, amount);
            }

            return 0;
        }

        @Override
        public boolean isResourceValid(T resource) {
            return this.backingView.isResourceValid(resource);
        }

        @Override
        public int getCapacity(T resource) {
            return this.backingView.getCapacity(resource);
        }

        @Override
        public T resource() {
            return this.backingView.resource();
        }

        @Override
        public int amount() {
            return this.backingView.amount();
        }
    }
}
