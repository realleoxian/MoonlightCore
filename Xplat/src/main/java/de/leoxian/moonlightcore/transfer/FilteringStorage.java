package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;

import java.util.Iterator;
import java.util.function.Supplier;

public abstract class FilteringStorage<T> implements Storage<T> {
    public static <T> Storage<T> insertOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, true, false);
    }

    public static <T> Storage<T> extractOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, false, true);
    }

    public static <T> Storage<T> readOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, false, false);
    }

    public static <T> Storage<T> of(Storage<T> backingStorage, boolean allowInsert, boolean allowExtract) {
        if(allowInsert && allowExtract) {
            return backingStorage;
        }

        return new FilteringStorage<T>(() -> backingStorage) {
            @Override
            protected boolean canInsert(T resource) {
                return allowInsert;
            }

            @Override
            protected boolean canExtract(T resource) {
                return allowExtract;
            }
        };
    }

    private final Supplier<Storage<T>> backingStorage;

    public FilteringStorage(Storage<T> backingStorage) {
        this(() -> backingStorage);
    }

    public FilteringStorage(Supplier<Storage<T>> backingStorage) {
        this.backingStorage = backingStorage;
    }

    @Override
    public int insert(TransactionContext context, T resource, int maxAmount) {
        if(this.canInsert(resource)) {
            return this.backingStorage.get().insert(context, resource, maxAmount);
        }

        return 0;
    }

    protected boolean canInsert(T resource) {
        return true;
    }

    @Override
    public int extract(TransactionContext context, T resource, int maxAmount) {
        if(this.canExtract(resource)) {
            return this.backingStorage.get().extract(context, resource, maxAmount);
        }

        return 0;
    }

    protected boolean canExtract(T resource) {
        return true;
    }

    @Override
    public @Nonnull StorageView<T> get(int index) {
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
    public @Nonnull Iterator<StorageView<T>> iterator() {
        return Iterators.transform(this.backingStorage.get().iterator(), FilteringStorageView::new);
    }

    private class FilteringStorageView implements StorageView<T> {
        private final StorageView<T> backingView;

        private FilteringStorageView(StorageView<T> backingView) {
            this.backingView = backingView;
        }

        @Override
        public int insert(TransactionContext context, T resource, int maxAmount) {
            if(canInsert(resource)) {
                return this.backingView.insert(context, resource, maxAmount);
            }

            return 0;
        }

        @Override
        public int extract(TransactionContext context, T resource, int maxAmount) {
            if(canExtract(resource)) {
                return this.backingView.extract(context, resource, maxAmount);
            }

            return 0;
        }

        @Override
        public int getCapacity(T resource) {
            return this.backingView.getCapacity(resource);
        }

        @Override
        public int getAmount() {
            return this.backingView.getAmount();
        }

        @Override
        public boolean isResourceBlank() {
            return this.backingView.isResourceBlank();
        }

        @Override
        public T getResource() {
            return this.backingView.getResource();
        }
    }
}
